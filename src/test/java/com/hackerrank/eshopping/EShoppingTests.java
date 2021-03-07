package com.hackerrank.eshopping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackerrank.eshopping.model.Product;
import com.hackerrank.eshopping.repository.ProductsRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class EShoppingTests {
    ObjectMapper om = new ObjectMapper();
    @Autowired
    ProductsRepository productsRepository;
    @Autowired
    MockMvc mockMvc;

    Map<String, Product> testData;

    @Before
    public void setup() {
        productsRepository.deleteAll();
        testData = getTestData();
    }

    @Test
    public void testAddProducts() throws Exception {
        //test new creation
        Product expectedRecord = testData.get("outfit_gown_true_1");
        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isCreated());

        Product actualRecord = productsRepository.findById(expectedRecord.getId()).get();
        Assert.assertTrue(new ReflectionEquals(expectedRecord).matches(actualRecord));

        expectedRecord = testData.get("footwear_shoes_false_1");
        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isCreated());

        actualRecord = productsRepository.findById(expectedRecord.getId()).get();
        Assert.assertTrue(new ReflectionEquals(expectedRecord).matches(actualRecord));

        //test existing
        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateProduct() throws Exception {
        //test existing
        Product expectedRecord = testData.get("outfit_gown_true_1");
        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isCreated());

        UpdateProductDto expectedUpdateRecord = new UpdateProductDto(24.6, 20.5, true);
        mockMvc.perform(put("/products/" + expectedRecord.getId())
                .contentType("application/json")
                .content(om.writeValueAsString(expectedUpdateRecord)))
                .andDo(print())
                .andExpect(status().isOk());

        expectedRecord.setRetailPrice(expectedUpdateRecord.getRetailPrice());
        expectedRecord.setDiscountedPrice(expectedUpdateRecord.getDiscountedPrice());
        expectedRecord.setAvailability(expectedUpdateRecord.getAvailability());

        Product actualRecord = productsRepository.findById(expectedRecord.getId()).get();
        Assert.assertTrue(new ReflectionEquals(expectedRecord).matches(actualRecord));

        //test non existing
        mockMvc.perform(put("/products/" + Long.MAX_VALUE)
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetProductWithId() throws Exception {
        Product expectedRecord = testData.get("outfit_nightgown_false_1");
        mockMvc.perform(post("/products")
                .contentType("application/json")
                .content(om.writeValueAsString(expectedRecord)))
                .andDo(print())
                .andExpect(status().isCreated());

        Product actualRecord = om.readValue(mockMvc.perform(get("/products/" + expectedRecord.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), Product.class);

        Assert.assertTrue(new ReflectionEquals(expectedRecord).matches(actualRecord));

        //non existing record test
        mockMvc.perform(get("/products/" + Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetProductByCategory() throws Exception {
        List<Product> expectedProductsWithCategoryFootwear = getTestData().values().stream().filter(p -> p.getCategory().equals("Footwear")).collect(Collectors.toList());
        List<Product> expectedProductsWithCategoryOutfit = getTestData().values().stream().filter(p -> p.getCategory().equals("Full Body Outfits")).collect(Collectors.toList());

        //post products of all category
        for (Product product : getTestData().values()) {
            mockMvc.perform(post("/products")
                    .contentType("application/json")
                    .content(om.writeValueAsString(product)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }
        Collections.sort(expectedProductsWithCategoryFootwear, Comparator.comparing(Product::getAvailability, Comparator.reverseOrder()).thenComparing(Product::getDiscountedPrice).thenComparing(Product::getId, Comparator.reverseOrder()));
        Collections.sort(expectedProductsWithCategoryOutfit, Comparator.comparing(Product::getAvailability, Comparator.reverseOrder()).thenComparing(Product::getDiscountedPrice).thenComparing(Product::getId, Comparator.reverseOrder()));

        //get cat: footwear
        List<Product> actualProductsWithCategoryFootwear = om.readValue(mockMvc.perform(get("/products?category=Footwear"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), new TypeReference<List<Product>>() {
        });

        //get cat: outfit
        List<Product> actualProductsWithCategoryOutfit = om.readValue(mockMvc.perform(get("/products?category=Full Body Outfits"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), new TypeReference<List<Product>>() {
        });

        for (int i = 0; i < expectedProductsWithCategoryOutfit.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expectedProductsWithCategoryOutfit.get(i)).matches(actualProductsWithCategoryOutfit.get(i)));
        }
        for (int i = 0; i < expectedProductsWithCategoryFootwear.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expectedProductsWithCategoryFootwear.get(i)).matches(actualProductsWithCategoryFootwear.get(i)));
        }

        //non existing record test
        mockMvc.perform(get("/products?category=" + Long.MAX_VALUE)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        Map<String, Product> testData = getTestData();

        List<Product> expectedRecords = new ArrayList<>(testData.values());
        for (Product product : expectedRecords) {
            mockMvc.perform(post("/products")
                    .contentType("application/json")
                    .content(om.writeValueAsString(product)))
                    .andDo(print())
                    .andExpect(status().isCreated());
        }
        Collections.sort(expectedRecords, Comparator.comparing(Product::getId));

        List<Product> actualRecords = om.readValue(mockMvc.perform(get("/products"))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(expectedRecords.size())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<Product>>() {
        });

        for (int i = 0; i < expectedRecords.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expectedRecords.get(i)).matches(actualRecords.get(i)));
        }
    }

    private Map<String, Product> getTestData() {
        Map<String, Product> data = new HashMap<>();

        Product outfit_gown_true_1 = new Product(
                1l,
                "Dressing Gown",
                "Full Body Outfits",
                303.0,
                251.49,
                true);
        data.put("outfit_gown_true_1", outfit_gown_true_1);
        Product outfit_gown_true_2 = new Product(
                2l,
                "Dressing Gown X",
                "Full Body Outfits",
                303.0,
                251.49,
                true);
        data.put("outfit_gown_true_2", outfit_gown_true_2);

        Product outfit_nightgown_false_1 = new Product(
                3l,
                "Nightgown",
                "Full Body Outfits",
                307.0,
                260.81,
                false);
        data.put("outfit_nightgown_false_1", outfit_nightgown_false_1);
        Product outfit_nightgown_false_2 = new Product(
                4l,
                "Nightgown X",
                "Full Body Outfits",
                307.0,
                254.81,
                false);
        data.put("outfit_nightgown_false_2", outfit_nightgown_false_2);

        Product footwear_shoes_true_1 = new Product(
                5l,
                "Shoes",
                "Footwear",
                150.0,
                130.0,
                true);
        data.put("footwear_shoes_true_1", footwear_shoes_true_1);
        Product footwear_shoes_true_2 = new Product(
                6l,
                "Shoes X",
                "Footwear",
                150.0,
                123.0,
                true);
        data.put("footwear_shoes_true_2", footwear_shoes_true_2);

        Product footwear_shoes_false_1 = new Product(
                7l,
                "Boots",
                "Footwear",
                162.0,
                132.84,
                false);
        data.put("footwear_shoes_false_1", footwear_shoes_false_1);
        Product footwear_shoes_false_2 = new Product(
                8l,
                "Boots X",
                "Footwear",
                162.0,
                132.84,
                false);
        data.put("footwear_shoes_false_2", footwear_shoes_false_2);

        return data;
    }

    private class UpdateProductDto {
        private Double retailPrice;
        private Double discountedPrice;
        private Boolean availability;

        public UpdateProductDto(Double retailPrice, Double discountedPrice, Boolean availability) {
            this.retailPrice = retailPrice;
            this.discountedPrice = discountedPrice;
            this.availability = availability;
        }

        public Double getRetailPrice() {
            return retailPrice;
        }

        public Double getDiscountedPrice() {
            return discountedPrice;
        }

        public Boolean getAvailability() {
            return availability;
        }
    }
}

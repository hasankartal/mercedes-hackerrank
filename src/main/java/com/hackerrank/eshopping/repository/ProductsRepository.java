package com.hackerrank.eshopping.repository;

import com.hackerrank.eshopping.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Long> {

    @Query(
            "select  e From Product e " +
                    " where e.category = :category " +
                    " order by e.availability desc, e.discountedPrice asc , e.id desc "
    )
    List<Product> findByCategoryOrderByAvailabilityDiscountedPriceAscIdDesc(@Param("category") String category);
}

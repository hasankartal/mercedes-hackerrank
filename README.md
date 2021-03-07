## Environment:
- Java version: 1.8
- Maven version: 3.*
- Spring Boot version: 2.2.1.RELEASE

## Read-Only Files:
- src/test/*

## Data:
Each product is a JSON entry with the following keys:
```json
{
    "id": "Unique identifier of the product.",
    "name": "Name of the product.",
    "category": "Category of the product.",
    "retailPrice": "The recommended selling price of the product. The price is given up to two places of decimal.",
    "discountedPrice": "The current selling price of the product. The price is given up to two places of decimal.",
    "availability": "A boolean value that indicates whether the product is in stock (true) or out of stock (false)."
}
```
Example of a product JSON object:
```json
{
    "id": 1,
    "name": "Waterproof",
    "category": "Coats and Jackets",
    "retail_price": 274.0,
    "discounted_price": 230.16,
    "availability": true
}
```

## Requirements:
The `REST` service must expose the `/products` endpoint, which allows for managing the collection of product records in the following way:

`POST` request to `/products` :
* creates a new product.
* expects a JSON product object with a unique ID. You can assume that the given object is always valid.
* if a product with the same ID already exists, the response code is 400 otherwise the response code is 201.

`PUT` request to `/products/{id}`:
* updates the product with give ID. the product JSON sent in request body will have the following keys `retailPrice, discountedPrice, availability`.
* if the product with the requested ID does not exist, the response code is 400 otherwise the response code is 200.

`GET` request to `/products/{id}`:
* returns a record with the given ID and status code 200.
* if there is no record in the database with the given ID, the response code is 404.

`GET` requests to `/products?category={category}`:
* returns all the products by the given category. 
* response code should be 200. 
* records should be sorted by the availability, in stock products must be listed before out of stock products. the products with same availability status must be sorted by the discounted price in the ascending order. finally, the products with same discounted price must be sorted by the ID in the descending order.

`GET` request to `/products`:
* returns all the records with status code 200.
* records should be sorted by ID in the ascending order.


Complete the given project so that it passes all the test cases when running the provided JUnit tests. The project by default supports the use of H2 database.

## Commands
- run: 
```bash
mvn clean package; java -jar target/eshopping-1.0.jar
```
- install: 
```bash
mvn clean install
```
- test: 
```bash
mvn clean test
```

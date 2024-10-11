# AdminV2

## Changes

### card-rest-controller

* **POST** /card
  * The cards are saved when the member is saved. **POST** /member
* **GET** /card/{id}
  * When the member is retrieved, it comes with its cards. **GET** /member/{id}
* **DELETE** /card/{id}
  * Delete the cards in the member and save the member. **POST** /member
* **GET** /card/by-member-id/{memberId}
  * When the member is retrieved, it comes with its cards. **GET** /member/{id}

## Findings

### @Transactional
* Cannot be used, it throws the following error: 
  `Caused by: com.mongodb.MongoQueryException: Command failed with error 20 (IllegalOperation): 'Transaction numbers are only allowed on a replica set member or mongos' on server localhost:34271`
  * See [Mongodb v4.0 Transaction, MongoError: Transaction numbers are only allowed on a replica set member or mongos](https://stackoverflow.com/a/51462024/1066054)
  * This example is interesting:
    `transactionTemplate.executeWithoutResult(ignore -> dogHasHandlers.forEach(this::setDogAndMemberIfTheyAreNull));`
    * See `casp.web.backend.business.logic.layer.dog.DogHasHandlerServiceImpl.getDogHasHandlersByIds`
    * See how to configure a TransactionTemplate on commit: `#1 Remove unneeded MongoClient`

### How to Unit Test an Abstract Class

See [How to Unit Test an Abstract Class](https://enterprisecraftsmanship.com/posts/how-to-unit-test-an-abstract-class/)

    ...  test class per concrete production class, where you create a test class per each concrete class of the hierarchy.

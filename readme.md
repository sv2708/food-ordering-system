# Food Ordering System

Its a multi-module maven project

## Common

This module consists of root level entities and other entities that are common for all the modules in the project.
It has the following packages
1. Entity
2. Event
3. Exception
4. ValueObject

ValueObjects are wrapper objects that are used for representing the field values in the system.

>Eg: Money = ValueObject wrapper for bigdecimal


The entities in this module will be extended by the other modules and provides implementation.


## Order Service

This Service is responsible for everything related to the order.

### Order Domain Core
This module is contains all the entity objects related to the Order Service. It consists of
- Validation logic of the entities
- Event Objects that will be passed for communication
- Custom Exceptions
- Value Objects

[OrderDomainServiceImpl](order-service/order-domain/order-domain-core/src/main/java/org/sarav/food/order/service/domain/OrderDomainServiceImpl.java)
This is the implementation class for validating the values in order related entities.


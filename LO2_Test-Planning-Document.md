# Test-Planning Document
Design and implement comprehensive test plans with instrumented code.


## Requirements
- R1: Order validation.
- R2: Drone navigation.
- R3: The service will read and use data from the given REST API.
- R4: The system must produce responses in less than 60s.

## Priority and Pre-requisites
- R1 and R2: These are the core function of the system, validating orders and providing a path for the drones.
  - R1 is prioritised since drone navigation should not even happen if an invalid order is provided, therefore this one should be tested first. Approach would be to unit test the individual validators and integration tests for the `validateOrder` endpoint.
  - R2 would come after and would be approached with unit tests for different cases of positions and obstacles. Then integration tests for `calcDeliveryPath`.
  - Then test both at the same time at the system level, to ensure the workflow of validating first and then providing navigation.
- R3: The system depends heavily on the information obtained from the REST API since elements like restaurants and their menu items to obstacles in space are retrieved from it.
- R4: This could not be tested without the previous being met.

## Test Plan
Here I detail what is to be tested for each requirement in order of priority
- R1: The system will check the validity of orders
  - When an order is placed it assesses its validity through several methods
  - Since there is a clear definition of each individual requirement for the makeup of an order, we can unit test each of the methods that handle each. Afterwards we can test the order validation as a system.
  - For the requirement to be met we would need these to pass and ensure that they work well together to properly validate orders.
  - For an order to be valid we need to verify:
    - That the credit card is valid.
    - That the ordered pizza exists in a restaurant's menu.
    - That the price with which the pizza was ordered is correct.
    - That the total price of the full order is correct (+100 pence delivery fee).
    - That the total amount of pizzas ordered does not exceed 4.
    - That pizzas are ordered from a sole restaurant.
    - That the restaurant is not closed on the order day.
    - That the order is not empty.
  - Tests for these can be carried out with unit testing and manually making random orders to ensure these are verified.
  - Then orders can be generated on a higher bulk for system testing.
  - Most of these require information to be retrieved from the REST API server for verification. So we would need to ensure that this is thoroughly tested with R3.
  
- R2: The system will provide navigation for the drone.
  - This is essential to the project. It will be used to define the paths the drone will take between restaurants and Appleton Tower to deliver the pizzas.
  - There are a few essential functions that will be tested at the unit level. We will afterwards test as a system.
  - The system needs to provide:
    - The distance in degrees between a pair of points in Longitude and Latitude.
    - Whether two points are close together. *Close* is defined as a set distance of 0.00015 degrees or less.
    - The next position for a point given an angle. The drone is set to move 0.00015 degrees per move.
    - Whether a point is within a given region, including its border, defined as polygon with a set of points as its vertices.
  - These can be unit tested with manually generated values.
  - R1 and R2 will need to be tested together after unit testing is concluded since a path should not be calculated without being given a valid order first. All the information for both requirements will be contained in the order and checked with the REST API server.
  - These two are the main priority for testing to ensure they are solid before moving to anything else.

- R3: The system will read data from our given REST API server.
  - This requirement is important since it is what we check against to validate orders in R1 and where we get the coordinates of objects from for pathfinding in R2.
  - It also contains a useful list of extensive order examples (valid and invalid) which we can retrieve for testing.
  - This requirement is mainly to be tested at the integration level since other systems rely on it, but we can do some unit testing to ensure communication with the REST API server is fully functioning.
  - The system must:
    - Fetch restaurant data, which includes name, coordinates, menu items and opening days which will be used for order validation (R1) and drone navigation (R2).
    - Fetch the University's Central Area coordinates.
    - Fetch the no-fly zones' coordinates.
  - We can unit test each of the data points we are fetching.
  - Then we can continue with some integration testing on `InputValidator.java` and `PathCalculator.java` which rely on the fetched data.

- R4: The system must produce a response to a request in less than 60s.
  - Not as prioritised, but it should be met for efficiency.
  - We need a completed system so testing will likely be late (hence why it is not a main priority)
  - Easily testable with system `MockMVC` tests.
  - It will require the most amount of testing in the pathfinding endpoint, but we can mostly assume that all coordinates provided will be reasonably close together (within Edinburgh) since the drones are to operate exclusively around the university campus.
  - So randomised coordinates for tests (around the area) as well as some carefully crafted ones will be generated to test performance.


## Scaffolding and Instrumentation
Here we describe the scaffolding and implementation needed in order to carry out the given tasks.
It will be helpful to scaffold sample orders and restaurants. For R1 we have a big list of orders that we can fetch from the REST API server and can be used as scaffolding for testing. It helps that they are made to have all sorts of errors present in them as well as some valid orders.
For R2 we will also use scaffolding to generate random coordinates for the drone navigation system, but most importantly some carefully selected coordinates for no-fly zones like dead alleyways.
There will be no scaffolding for the rest of the requirements.
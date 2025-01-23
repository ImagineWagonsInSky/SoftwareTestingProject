# Learning Outcome 1
This document analyses the requirements to determine appropriate testing strategies.

## Range of requirements
Functional requirements specify a function that our system must be able to perform:
- The system will check the validity of orders.
- The system must not allow for pathfinding to be carried out for invalid orders.
- The system will provide navigation for the drone in the form of functions about its position and pathfinding.
- The system will read data from our given REST API server.
- The system will not allow the drone to enter no-fly zones.
- The system will be able to convert paths to GEOJson.
- The system will provide moves for the drone of 0.00015 degrees each.
- The drone should deliver one order at a time, with the number of pizzas per order being between 1 and 4.
- The system will serve as a RESTful service in which the functionalities will be provided through HTTP endpoints.
- Once a drone goes into the university central area, it should not leave on that delivery.

Non-functional requirements describe how the software will perform what it does.
- The system must produce responses in less than 60s.
- The system should be easy to use
- Must be timely, where the time from placing order to arrival is short enough to ensure pizza is hot.
- The system must deliver correct orders to the right users.
- The system must be built in a way for it to allow for scalability.
- The system should check the requests for correctness.

## Level of requirements
I have picked and grouped 4 of the above requirements to be used for testing since they will provide sufficient coverage.
They are:
- R1: The system will check the validity of orders
- R2: The system will provide navigation for the drone.
- R3: The system will read data from our given REST API server.
- R4: The system must produce a response to a request in less than 60s.

These will require system level testing since they are system-level requirements. They all have unit and integration level requirements.
So for R1 for instance, a simple unit level requirement is that credit cards must not be expired. 
For R2 we have the unit requirement that no coordinates should be in no-fly zones.
For R3 we have the unit requirement that the system must be able to fetch restaurant data.
All of these have integration level requirements since they depend on each other (e.g. pathfinding should not happen with an invalid order).

## Test approach for requirements
The general approach to the 4 picked out requirements will be the same: unit testing, integration testing and system testing for unit-level requirements, integration-level requirements and system-level requirements respectively.
If any fail while developing, all tests should be carried out from the start until all tests pass.
Our measurable requirement will be tested to uphold afterwards.

## Appropriateness of testing approach
The testing approach is a good one considering the requirements we outlined because they quite naturally fall into the three level of requirement categories.
Within the bounds of what we had to do in Pizzadronz, there are no significant issues. But if the system were to be scaled, we run into various limitations.
One is security which can't be tested within our scope but might rely on us upon scalability.
Another limitation is testing the pathfinding algorithm's correctness, where we mostly look at it through its visual representation to ensure no-fly zones are not traversed, so it does not make randomly generated coordinates usable for testing.
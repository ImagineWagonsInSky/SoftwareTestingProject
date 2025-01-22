# Testing Evidence

## Tests in the repository
This is an outline of each of the classes used for testing:
- `InputValidatorTest.java`
  - This one covers the unit testing for each of the order validation endpoints, so R1.
- `ValidateOrderMockMvcTest.java`
  - This one covers the order validation testing with the big list of orders provided for us to fetch. It is more of integration testing since it also relies on the fetching mechanism for which mocking is used. So R1 and R3 are tested here.
- `DistanceToControllerTest.java`
  - This class tests the `distanceTo` endpoint pertaining to R2 at the unit level.
- `PolygonControllerTest.java`
  - This class tests the `isInRegion` endpoint pertaining to R2 with unit testing.
- `PathCalculationTest.java`
  - This one tests the actual path calculation endpoint for R2 with manually generated values to test its correctness and performance in different scenarios.

## Testing techniques
### Functional testing
Functional testing  focused on ensuring that the implemented features met the specified requirements and behaved correctly under various scenarios.

For R1 it ensured that invalid orders were rejected with appropriate order codes and status. For R2 we made sure that the pathfinding algorithm calculated valid delivery paths between restaurant and destination, avoiding no-fly zones and staying within the central area. For R3 we verified correct interaction with external REST services to fetch restaurant data, no-fly zones, and central area boundaries; by mocking these services during tests to ensure resilience and validate fallback behaviour.

### Structural testing
Structural testing in PizzaDronz ensured the correctness of internal logic, workflows, and code coverage. Unit tests were written for components like `InputValidator` and `PathCalculator` to validate their behaviours. Service integration was verified by testing interactions between services such as `PathCalculationService`, `PathDataService`, and `PizzaService`, ensuring data was fetched and processed correctly.
The A* pathfinding algorithm was rigorously tested for adherence to constraints like no-fly zones and central areas while ensuring termination near the goal. The modular design was validated to confirm proper separation of concerns, with services handling core logic and controllers focusing on request handling.
These tests collectively validated the application’s architecture, ensuring it was robust, maintainable, and aligned with design principles.

### Model-based testing
Model-based testing was performed for the pathfinding logic using the A* algorithm. The model represented the drone's movements within constraints like no-fly zones, central areas, and compass-based neighbour generation. By simulating different start and goal scenarios, we validated the algorithm's adherence to the model's rules, ensuring paths avoided restricted regions and stayed within bounds. This testing confirmed that the implementation correctly followed the defined constraints and produced optimal results under varying conditions.

## Evaluation criteria
The adequacy of the testing in the PizzaDronz project is evaluated based on functional correctness, ensuring that all endpoints behave as expected under a wide range of valid and invalid inputs. Key aspects include validating that orders are correctly classified as valid or invalid, credit card details are accurately processed, and pathfinding adheres to constraints like no-fly zones and central areas. Coverage of all functional requirements ensures that the system handles edge cases, such as incorrect pizza prices or empty orders, while delivering meaningful error messages.

Structural adequacy focuses on code coverage, ensuring all paths in the application logic, including controllers, services, and utility classes, are tested. This includes testing interactions between components, such as the delegation of logic from controllers to services, and ensuring modularity does not introduce bugs. Additionally, the use of GeoJSON output for path visualization serves as an indirect structural test by providing a tangible way to evaluate whether calculated paths adhere to system constraints.

Performance and reliability criteria are also critical. The pathfinding algorithm should demonstrate efficiency, producing results within acceptable timeframes even for complex scenarios. Integration with external services should be robust, with appropriate error handling for scenarios like service unavailability. Testing adequacy is further strengthened by ensuring that tests are repeatable, automated, and capable of detecting regressions when new features are added or existing ones are updated.

## Results of testing and Evaluation of the results
All tests where passed. Some evidently failed many times before getting the whole lot of them to by changing things about the code.
In testing i found for example that the regions being fetched sometimes provided the first coordinate once at the start and once at the end which caused issues with the `isInRegion` endpoint.
I also found that the heuristic had to be tweaked because the algorithm took a very long time (>60s) when given coordinates that were close together. After the tweak, the algorithm takes about a second in average computing time.
I also found after checking the order validation against the randomly generated data that some order codes where not being returned correctly.
After these minor issues, all tests (including integration and system tests passed.
All tests passed and the coverage was good so i am happy with the results.


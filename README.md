# Border Crossing Simulation

## A fully functional EXAMPLE project written in JavaFX showing how a border crossing between two countries works.

This JavaFX project simulates an imaginary border crossing with three police terminals and two customs terminals on one side of the border between two countries. The simulation involves processing various types of vehicles, including personal vehicles, buses, and trucks, through police and customs controls.

## Simulation Overview

### Vehicle Types:

  - Personal vehicles (up to 5 passengers, including the driver)
  - Buses (capacity up to 52 passengers, including the driver)
  - Trucks (capacity up to 3 passengers, including the driver)

### Cargo and Documentation:

  - Trucks may have cargo requiring customs documentation (50% probability).
  - In 20% of trucks, the actual cargo weight is 30% higher than declared.
  - Buses have luggage space with a 70% probability that a passenger has luggage.
  - In 10% of cases, luggage in buses contains prohibited items.

### Passenger Documents:

  - Each passenger has an identification document.
  - 3% of passenger documents are invalid.

### Simulation Workflow:

  - Vehicles are queued randomly at the beginning of the simulation.
  - Police terminals process vehicles in parallel.
  - One police and customs terminal are reserved for trucks.
  - Vehicles wait for customs processing if they are processed at the police terminals.
  - Processing time is 0.5 seconds for personal vehicles and trucks, and 0.1 seconds for buses.

### Incident Handling:

  - 3% of passengers have invalid documents and are recorded in a penalty list.
  - Vehicles with invalid documents or a driver denied entry are removed from the queue. 

### Customs Processing:

  - Personal vehicles wait 2 seconds without additional checks.
  - Buses are processed per passenger, and luggage is checked for prohibited items.
  - Trucks generate customs documentation if needed and are tested for cargo weight.

## Implementation Details

### Parallel Processing:

  - Police and customs terminals can process vehicles in parallel.
  - Shared file with information on terminal availability.

### User Interface:

  - JavaFX graphical representation of the simulation.
  - Different colors/icons for each vehicle type.
  - Display of ongoing events and logs.

### Simulation Control:

  - Start, stop, and restart the simulation.
  - Display of simulation duration.

### Data Presentation:

  - Detailed vehicle and passenger information.
  - Separate interfaces for queued, processed, and incidents.

### Logger Class:

  - Exception handling using the Logger class in all classes.
  
## Running the Simulation

  1. Clone the repository.
  2. Open the project in your preferred Java IDE.
  3. Run the simulation script.
  4. Follow on-screen instructions for simulation control and monitoring.


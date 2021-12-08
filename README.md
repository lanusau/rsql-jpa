# rSQL JPA library

This library helps with creating Spring JPA Specifications from rSQL queries, which then can be used to query
data from Spring JPA repositories.

# Example

Declare repository as extending JpaSpecificationExecutor

    @Repository
    public interface CarRepository extends PagingAndSortingRepository<Car, Long>, JpaSpecificationExecutor<Car> {
    }

Wire in RSQLSpecificationFactory
    
    private final RSQLSpecificationFactory rsqlSpecificationFactory;

Use RSQLSpecificationFactory to create specification from rSQL query and pass it to repository

    public List<Car> getCars(String rSQL) {
        return carRepository.findAll(rsqlSpecificationFactory.createRSQLSpecification(rSQL, Car.class));
    }


package com.conversantmedia.mpub.rsql.jpa;

import com.conversantmedia.mpub.rsql.jpa.model.Car;
import com.conversantmedia.mpub.rsql.jpa.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DataJpaTest
@ActiveProfiles("test")
class RSQLJpaTest {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private RSQLSpecificationFactory rsqlSpecificationFactory;

    @BeforeEach
    void setUp() {
        rsqlSpecificationFactory = new RSQLSpecificationFactory(conversionService, entityManagerFactory);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("carQuery")
    void carQuery(String query, Predicate<List<Car>> predicate) {
        final List<Car> cars = carRepository.findAll(rsqlSpecificationFactory.createRSQLSpecification(query, Car.class));
        assertTrue(predicate.test(cars));
    }

    static Stream<Arguments> carQuery() {
        return Stream.of(
                // Operators
                arguments("modelId==11", match(car -> car.getModelId().equals(11L))),
                arguments("modelId==1*", match(car -> car.getModelId().toString().startsWith("1"))),
                arguments("color==WHITE", match( car -> car.getColor().equals(Car.Color.WHITE))),
                arguments("electric==true", match(Car::getElectric)),
                arguments("manufactureDate==2021-01-13", match(car -> car.getManufactureDate().equals(LocalDate.of(2021,1,13)))),
                arguments("dateSold==2021-10-02T00:00:00", match(car -> car.getDateSold().equals(LocalDateTime.of(2021,10,2,0,0,0)))),
                arguments("dateSold=ex=true", match(car -> car.getDateSold() != null)),
                arguments("dateSold=ex=false", match(car -> car.getDateSold() == null)),
                arguments("modelId!=11", match(car -> !car.getModelId().equals(11L))),
                arguments("color!=WHITE", match( car -> !car.getColor().equals(Car.Color.WHITE))),
                arguments("electric!=true", match(car -> !car.getElectric())),
                arguments("manufactureDate!=2021-01-13", match(car -> !car.getManufactureDate().equals(LocalDate.of(2021,1,13)))),
                arguments("dateSold!=2021-10-02T00:00:00", match(car -> !car.getDateSold().equals(LocalDateTime.of(2021,10,2,0,0,0)))),
                arguments("modelId=gt=5", match(car -> car.getModelId() > 5L)),
                arguments("manufactureDate=gt=2021-01-13", match(car -> car.getManufactureDate().isAfter(LocalDate.of(2021,1,13)))),
                arguments("dateSold=gt=2021-10-02T00:00:00", match(car -> car.getDateSold().isAfter(LocalDateTime.of(2021,10,2,0,0,0)))),
                arguments("modelId=ge=5", match(car -> car.getModelId() >= 5L)),
                arguments("manufactureDate=ge=2021-01-13", match(car -> car.getManufactureDate().isAfter(LocalDate.of(2021,1,12)))),
                arguments("dateSold=ge=2021-10-02T00:00:00", match(car -> car.getDateSold().isAfter(LocalDateTime.of(2021,10,1,0,0,0)))),
                arguments("color=in=(WHITE,SILVER)", match( car -> car.getColor().equals(Car.Color.WHITE) || car.getColor().equals(Car.Color.SILVER))),
                arguments("modelId=in=(5,8,10)", match(car -> car.getModelId().equals(5L) || car.getModelId().equals(8L) || car.getModelId().equals(10L))),
                arguments("color=out=(WHITE,SILVER)", match( car -> !car.getColor().equals(Car.Color.WHITE) && !car.getColor().equals(Car.Color.SILVER))),
                arguments("modelId=out=(5,8,10)", match(car -> !car.getModelId().equals(5L) && !car.getModelId().equals(8L) || car.getModelId().equals(10L))),
                arguments("modelId=lt=5", match(car -> car.getModelId() < 5L)),
                arguments("manufactureDate=lt=2021-01-13", match(car -> car.getManufactureDate().isBefore(LocalDate.of(2021,1,13)))),
                arguments("dateSold=lt=2021-10-02T00:00:00", match(car -> car.getDateSold().isBefore(LocalDateTime.of(2021,10,2,0,0,0)))),
                arguments("modelId=le=5", match(car -> car.getModelId() <= 5L)),
                arguments("manufactureDate=le=2021-01-13", match(car -> car.getManufactureDate().isBefore(LocalDate.of(2021,1,14)))),
                arguments("dateSold=le=2021-10-02T00:00:00", match(car -> car.getDateSold().isBefore(LocalDateTime.of(2021,10,2,0,0,1)))),
                // Joins
                arguments("model.year==2021", match(car -> car.getModel().getYear() == 2021)),
                arguments("model.name==Model*", match(car -> car.getModel().getName().startsWith("Model"))),
                arguments("model.manufacturer.name==Tesla", match(car -> car.getModel().getManufacturer().getName().equals("Tesla"))),
                // Complex queries
                arguments("electric!=true;model.year=gt=2020", match(car -> !car.getElectric() && car.getModel().getYear() > 2020)),
                arguments("(model.manufacturer.name==Tesla,electric==true);model.year==2021", match(car -> (car.getModel().getManufacturer().getName().equals("Tesla") || car.getElectric()) && car.getModel().getYear() == 2021))
        );
    }

    private static Predicate<List<Car>> match(Predicate<Car> predicate) {
        return cars -> cars.stream().allMatch(predicate);
    }
}

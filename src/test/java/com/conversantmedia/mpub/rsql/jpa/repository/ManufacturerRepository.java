package com.conversantmedia.mpub.rsql.jpa.repository;

import com.conversantmedia.mpub.rsql.jpa.model.Car;
import com.conversantmedia.mpub.rsql.jpa.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturerRepository extends PagingAndSortingRepository<Manufacturer, Long>, JpaSpecificationExecutor<Car> {
}

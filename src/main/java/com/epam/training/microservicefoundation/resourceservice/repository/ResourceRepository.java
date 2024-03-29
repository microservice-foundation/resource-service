package com.epam.training.microservicefoundation.resourceservice.repository;

import com.epam.training.microservicefoundation.resourceservice.model.Resource;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends BaseJpaRepository<Resource, Long>, BaseRepository<Resource> {

}

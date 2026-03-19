package com.FleetStudio.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.FleetStudio.Entity.County;

public interface CountyRepository extends JpaRepository<County, String> {

    //@Query("SELECT c FROM County c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(c.state) LIKE LOWER(CONCAT('%', :state, '%'))")
    //List<County> search(@Param("name") String name, @Param("state") String state);

    List<County> findByStateIgnoreCase(String state);
    List<County> findByNameContainingIgnoreCase(String name);
    List<County> findByNameContainingIgnoreCaseAndStateIgnoreCase(String name, String state);
}


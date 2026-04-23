package com.example.lab01_hyrule.repository;


import com.example.lab01_hyrule.model.Material;
import java.util.List;

public interface MaterialRepository {
    List<Material> findAll();
}

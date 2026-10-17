package com.northstar.supplychainos.service;

import com.northstar.supplychainos.dto.WarehouseDto;
import com.northstar.supplychainos.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class WarehouseService {
  private final WarehouseRepository warehouses;
  public WarehouseService(WarehouseRepository warehouses) { this.warehouses = warehouses; }

  public List<WarehouseDto> listActive() {
    return warehouses.findAllByActiveTrueOrderByCodeAsc().stream()
        .map(w -> new WarehouseDto(w.getId(), w.getCode(), w.getName(), w.getCity(),
            w.getState(), w.getCountry(), w.getTimezone(), w.isActive()))
        .toList();
  }
}

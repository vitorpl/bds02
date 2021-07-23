package com.devsuperior.bds02.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds02.dto.CityDTO;
import com.devsuperior.bds02.entities.City;
import com.devsuperior.bds02.exceptions.DatabaseException;
import com.devsuperior.bds02.exceptions.ResourceNotFoundException;
import com.devsuperior.bds02.repositories.CityRepository;

@Service
public class CityService {

	@Autowired
	private CityRepository repository;
	
	@Transactional
	public CityDTO update(Long id, CityDTO dto) {
		try {
			City city = repository.getOne(id);
			city.setName(dto.getName());
			city = repository.save(city);
			return new CityDTO(city);
		}
		catch(EntityNotFoundException ex) {
			throw new ResourceNotFoundException("Id not found "+ id);
		}
	}

	// Esse cara não gosta do @ Transactional para testar / acaba não passando no teste
	public void delete(Long id) {
		try {
			repository.deleteById(id);		
		}
		catch(EmptyResultDataAccessException ex) {
			throw new ResourceNotFoundException("Id não encontrado: "+ id);
		}
		catch(DataIntegrityViolationException dataEx) {
			//com o @Transactional não cai aqui
			throw new DatabaseException("Recurso não pode ser excluído pois possui registros dependentes");
		}
	}
	
	@Transactional
	public List<CityDTO> findAll() {
		List<City> list = repository.findAll(Sort.by("name"));
		return list.stream().map(ct -> new CityDTO(ct)).collect(Collectors.toList());
	}
}

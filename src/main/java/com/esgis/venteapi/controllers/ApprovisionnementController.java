package com.esgis.venteapi.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esgis.venteapi.models.Approvisionnement;
import com.esgis.venteapi.models.Boutique;
import com.esgis.venteapi.models.Produit;
import com.esgis.venteapi.models.AgentVendeur;
import com.esgis.venteapi.services.ApprovisionnementService;
import com.esgis.venteapi.services.BoutiqueService;
import com.esgis.venteapi.services.AgentVendeurService;
import com.esgis.venteapi.services.ProduitService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/supplies")
public class ApprovisionnementController {

	@Autowired
	private ApprovisionnementService service;

	@Autowired
	private BoutiqueService storeService;

	@Autowired
	private ProduitService prodService;

	@Autowired
	private AgentVendeurService sellerService;

	@PostMapping("/new")
	public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody Approvisionnement supply) {
		if (supply.getDateStock().before(new Date(System.currentTimeMillis()))
				|| supply.getQuantiteStock() < 1) {
			return ResponseEntity.badRequest().body(Map.of("message", "Invalid dateStock or quantiteStock."));
		}
		//
		final Optional<Boutique> store = storeService.findById(supply.getStoreId());
		if (store == null) {
			return ResponseEntity.badRequest().body(Map.of("message", "This store doesn't exist."));
		}
		//
		final Optional<Produit> product = prodService.findById(supply.getProduitId());
		if (product == null) {
			return ResponseEntity.badRequest().body(Map.of("message", "This product doesn't exist."));
		}
		//
		final Optional<AgentVendeur> seller = sellerService.findById(supply.getSellerId());
		if (seller == null) {
			return ResponseEntity.badRequest().body(Map.of("message", "This seller doesn't exist."));
		}
		//
		final Approvisionnement data = service.create(supply);
		return new ResponseEntity<>(Map.of("message", "Success", "data", data), HttpStatus.CREATED);
	}

	@GetMapping("/all")
	public List<Approvisionnement> findAllApprovisionnements() {
		return service.findAll();
	}

	@GetMapping("/find/{id}")
	public Approvisionnement findOneApprovisionnements(@PathVariable String id) {
		Optional<Approvisionnement> supply = service.findById(id);
		if (supply.isPresent()) {
			return supply.get();
		}
		return null;
	}

	@PutMapping("/update/{id}")
	public Approvisionnement updateApprovisionnement(@PathVariable String id, @RequestBody Approvisionnement supply) {
		Optional<Approvisionnement> optional = service.findById(id);

		if (optional.isPresent()) {
			Approvisionnement data = optional.get();
			data.setId(id);

			if (supply.getDateStock() != null) {
				data.setDateStock(supply.getDateStock());
			}
			if (supply.getProduitId() != null) {
				data.setProduitId(supply.getProduitId());
			}
			if (supply.getQuantiteStock() > 0) {
				data.setQuantiteStock(supply.getQuantiteStock());
			}
			if (supply.getSellerId() != null) {
				data.setSellerId(supply.getSellerId());
			}
			if (supply.getStoreId() != null) {
				data.setStoreId(supply.getStoreId());
			}
			return service.update(data);
		} else {
			return null;
		}
	}

	@DeleteMapping("/delete/{id}")
	public void deleteApprovisionnement(@PathVariable String id) {
		service.delete(id);
	}

}

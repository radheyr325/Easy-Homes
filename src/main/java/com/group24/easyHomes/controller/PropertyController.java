package com.group24.easyHomes.controller;

import com.group24.easyHomes.dto.PropertyDTO;
import com.group24.easyHomes.mappers.PropertyDTOToProperty;
import com.group24.easyHomes.model.Property;
import com.group24.easyHomes.model.PropertyImages;
import com.group24.easyHomes.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class PropertyController {

    @Autowired
    private PropertyService  service;


    @Autowired
    private PropertyDTOToProperty propertyDTOToProperty;

    @GetMapping("/properties")
    public ResponseEntity<List<Property>> list()
    {

        return new ResponseEntity<>(service.listAll(),HttpStatus.OK);
    }

   @PostMapping(value = "/properties",consumes = {"multipart/form-data"},produces ={"application/json"})
    public ResponseEntity<Property> addProperty(@RequestPart("property") @Valid PropertyDTO propertyDTO,
                                                @RequestPart("file")  MultipartFile[] files) {
        try {

            Set<PropertyImages> propertyImages = new HashSet<>();
            Property property = propertyDTOToProperty.convert(propertyDTO);

            for(MultipartFile file:files)
            {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                PropertyImages image = new PropertyImages(fileName, file.getContentType(), file.getBytes());
                property.addImage(image);
                //image.setProperty(property);
            }

            service.addProperty(property);
            return new ResponseEntity<>(property,HttpStatus.CREATED) ;
         } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST) ;

         }
    }

    @DeleteMapping(value = "/properties/{propertyId}")
    public ResponseEntity<HttpStatus>  removeProperty(@PathVariable int propertyId)
    {
        if("SUCCESS".equalsIgnoreCase(service.delete(propertyId))) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @PutMapping(value = "/properties/{propertyId}/update",consumes = {"application/json"},produces ={"application/json"})
    public ResponseEntity<Property>  updateProperty(@PathVariable int propertyId, @RequestBody Property property)
    {
            return new ResponseEntity<>(service.updateProperty(propertyId,property),HttpStatus.NO_CONTENT);

    }
/*
    @PatchMapping(value = "/properties/{propertyId}")
    public ResponseEntity<Property>  updateProperty(@PathVariable int propertyId, @RequestBody Map<Object,Object> fields)
    {
        Property property = service.getProperty(propertyId);
        fields.forEach((k,v) ->
        {
            Field field = ReflectionUtils.findField(Property.class,(String) k);
            if(field != null)
            {
                field.setAccessible(true);
                ReflectionUtils.setField(field,property,v);

            }
        });
        service.addProperty(property);
        return new ResponseEntity<Property>(property,HttpStatus.OK);
    }


       @PatchMapping(value = "/properties/address/{addressID}" ,consumes = {"application/json"})
        public void updatePropertyAddress(@PathVariable int addressID, @RequestBody PropertyAddressDTO addressDTO)
        {
            PropertyAddress existingAddress =  addressRepository.findById(addressID).get();
            System.out.println(existingAddress.getLocation());

            PropertyAddress updatedAddress = addressService.convertDTOToEntity(addressDTO);
            //System.out.println(propertyAddress.getLocation());
            existingAddress = updatedAddress;
            addressRepository.save(existingAddress);
            //return new ResponseEntity<PropertyAddress>(address, HttpStatus.OK);
        }*/

}

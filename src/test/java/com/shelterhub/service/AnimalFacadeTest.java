package com.shelterhub.service;

import com.shelterhub.database.AnimalRepository;
import com.shelterhub.domain.model.Animal;
import com.shelterhub.dto.AnimalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.shelterhub.utils.AnimalUtils.buildAnimalDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnimalFacadeTest {
    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private AnimalFacade animalFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnCreatedAnimalDTO() {
        AnimalDTO animalDTO = buildAnimalDTO(false);
        Animal animal = buildAnimal(animalDTO);

        when(animalRepository.save(any(Animal.class))).thenReturn(animal);

        AnimalDTO result = animalFacade.create(animalDTO);

        assertEquals(animal.getName(), result.getName());
        assertEquals(animal.getAge(), result.getAge());
        assertEquals(animal.getAnimalType(), result.getAnimal_type());
        assertEquals(animal.getMedicalRecordId(), result.getMedical_record_id());
        verify(animalRepository, times(1)).save(any(Animal.class));
    }

    @Test
    public void shouldReturnUpdatedAnimalDTO() {
        AnimalDTO substitutedAnimalDTO = buildAnimalDTO(true);
        AnimalDTO animalDTO = buildAnimalDTO(true);
        Animal substitutedAnimal = buildAnimal(substitutedAnimalDTO);
        Animal animal = buildAnimal(animalDTO);

        when(animalRepository.getReferenceById(animalDTO.getId())).thenReturn(substitutedAnimal);
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);

        AnimalDTO result = animalFacade.update(animalDTO, animalDTO.getId());

        assertEquals(animalDTO.getId(), result.getId());
        assertEquals(animalDTO.getName(), result.getName());
        assertEquals(animalDTO.getAge(), result.getAge());
        assertEquals(animalDTO.getAnimal_type(), result.getAnimal_type());
        assertEquals(animalDTO.getMedical_record_id(), result.getMedical_record_id());
        verify(animalRepository, times(1)).save(any(Animal.class));
        verify(animalRepository, times(1)).getReferenceById(animalDTO.getId());
    }

    @Test
    public void shouldReturnListOfAnimalDTOs() {
        Animal animal1 = buildAnimal(buildAnimalDTO(true));
        Animal animal2 = buildAnimal(buildAnimalDTO(true));
        List<Animal> animals = Arrays.asList(animal1, animal2);

        when(animalRepository.findAll()).thenReturn(animals);

        List<AnimalDTO> result = animalFacade.getAllAnimals();

        assertEquals(animals.size(), result.size());
        verify(animalRepository, times(1)).findAll();
    }

    @Test
    public void shouldReturnAnimalById() {
        AnimalDTO animalDTO = buildAnimalDTO(true);
        Animal animal = buildAnimal(animalDTO);

        when(animalRepository.findById(animalDTO.getId())).thenReturn(Optional.of(animal));

        Optional<AnimalDTO> result = animalFacade.getAnimalById(animalDTO.getId());

        assertEquals(animalDTO.getId(), result.get().getId());
        assertEquals(animalDTO.getName(), result.get().getName());
        assertEquals(animalDTO.getAge(), result.get().getAge());
        assertEquals(animalDTO.getAnimal_type(), result.get().getAnimal_type());
        assertEquals(animalDTO.getMedical_record_id(), result.get().getMedical_record_id());
        verify(animalRepository, times(1)).findById(animalDTO.getId());
    }

    @Test
    public void shouldDeleteAnimal() {
        AnimalDTO animalDTO = buildAnimalDTO(true);
        Animal animal = buildAnimal(animalDTO);

        when(animalRepository.findById(animalDTO.getId())).thenReturn(Optional.of(animal));

        String result = animalFacade.delete(animalDTO.getId());

        verify(animalRepository, times(1)).deleteById(animalDTO.getId());
        verify(animalRepository, times(1)).findById(animalDTO.getId());
        assertEquals("Animal " + animalDTO.getId() + " was deleted successfully", result);
    }

    @Test
    public void shouldNotDeleteAnimalIfAnimalNotFound() {
        AnimalDTO animalDTO = buildAnimalDTO(true);
        Animal animal = buildAnimal(animalDTO);

        when(animalRepository.findById(animalDTO.getId())).thenReturn(Optional.empty());

        String result = animalFacade.delete(animalDTO.getId());

        verify(animalRepository, times(0)).deleteById(animalDTO.getId());
        verify(animalRepository, times(1)).findById(animalDTO.getId());
        assertEquals("Animal not found", result);
    }

    private Animal buildAnimal(AnimalDTO animalDTO) {
        Animal animal = new Animal();
        animal.setId(animalDTO.getId() != null ? animalDTO.getId() : UUID.randomUUID());
        animal.setName(animalDTO.getName());
        animal.setAge(animalDTO.getAge());
        animal.setMedicalRecordId(animalDTO.getMedical_record_id());
        return animal;
    }
}
package fr.abes.licencesnationales.core.entities.editeur;

import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;

class EditeurEntityTest {

    private Validator validator;

    @BeforeEach
    void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("test validation creation editeur Blank ")
    void testValidateCreatEditeurBlank() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        Set<TypeEtablissementEntity> setType = new HashSet<>();
        setType.add(type);

        EditeurEntity editeur = new EditeurEntity(1,"","","",setType);

        Set<ConstraintViolation<EditeurEntity>> violationsEditeur = validator.validate(editeur);
        Assertions.assertEquals(2,violationsEditeur.size());
        Assertions.assertEquals("Le nom de l'éditeur fourni n'est pas valide", violationsEditeur.stream().filter(v -> v.getPropertyPath().toString().equals("nom")).findFirst().get().getMessage());
        Assertions.assertEquals("L'adresse postale fournie n'est pas valide", violationsEditeur.stream().filter(v -> v.getPropertyPath().toString().equals("adresse")).findFirst().get().getMessage());
    }

    @Test
    @DisplayName("test validation creation editeur Null ")
    void testValidateCreatEditeurNull() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "Nouveau");
        Set<TypeEtablissementEntity> setType = new HashSet<>();
        setType.add(type);

        EditeurEntity editeur = new EditeurEntity(1,null,null,null,setType);

        Set<ConstraintViolation<EditeurEntity>> violationsEditeur = validator.validate(editeur);
        Assertions.assertEquals(2,violationsEditeur.size());
        Assertions.assertEquals("Le nom de l'éditeur fourni n'est pas valide", violationsEditeur.stream().filter(v -> v.getPropertyPath().toString().equals("nom")).findFirst().get().getMessage());
        Assertions.assertEquals("L'adresse postale fournie n'est pas valide", violationsEditeur.stream().filter(v -> v.getPropertyPath().toString().equals("adresse")).findFirst().get().getMessage());
    }
}
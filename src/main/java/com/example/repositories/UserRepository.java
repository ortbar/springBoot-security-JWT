package com.example.repositories;


import com.example.models.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    //necesitamos crear un metodo especial que no trae crudRepository. Buscar un usuario por el nombre
    Optional<UserEntity> findByUsername(String username);


    // DOS FORMAS DE HACERLO. USANDO LA FIRMA DEL METODO O DANDOLE UN NOMBRE ESPECIFICO AL METODO

//    Optional<UserEntity> findByUserName(String username);

    // si queremos darle otro nombre personalizado...usando @Query
    @Query("select u from UserEntity u where u.username = ?1")
    Optional<UserEntity> getName(String username);


}

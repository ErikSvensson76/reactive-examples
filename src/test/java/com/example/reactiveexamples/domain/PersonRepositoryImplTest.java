package com.example.reactiveexamples.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;


class PersonRepositoryImplTest {

    PersonRepositoryImpl testObject;

    @BeforeEach
    void setUp() {
        testObject = new PersonRepositoryImpl();
    }

    @Test
    void getByIdBlock() {
        var resultMono = testObject.getById(1);

        Person person = resultMono.block();
        System.out.println(person);
    }

    @Test
    void getByIdSubscribe() {
        var personMono = testObject.getById(4);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        personMono.subscribe(System.out::println);
    }

    @Test
    void getByIdMapFunction() {
        var personMono = testObject.getById(1);

        //Will never print out person due to no subscription
        personMono.map(person -> {
            System.out.println(person);
            return person.getFirstName();
        });

        personMono.map(Person::getFirstName)
                .subscribe(System.out::println);
    }

    @Test
    void fluxTestBlockFirst() {
        Flux<Person> personFlux = testObject.findAll();

        Person person = personFlux.blockFirst();

        System.out.println(person);

    }

    @Test
    void fluxSubscribe() {
        testObject.findAll().subscribe(System.out::println);
    }

    @Test
    void fluxToListMono() {
        var personFlux = testObject.findAll();

         Mono<List<Person>> personListMono = personFlux.collectList();

         personListMono.subscribe(list -> list.forEach(System.out::println));
    }

    @Test
    void findPersonById() {
        Flux<Person> personFlux = testObject.findAll();

        Mono<Person> personMono = personFlux
                .filter(person -> person.getId() == 3)
                .next();

        personMono.subscribe(System.out::println);
    }

    @Test
    void findPersonByIdNotFound() {
        Flux<Person> personFlux = testObject.findAll();

        Mono<Person> personMono = personFlux
                .filter(person -> person.getId() == 310)
                .next();

        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();

        personMono.subscribe(System.out::println);
    }

    @Test
    void findPersonByIdNotFoundWithException() {
        Flux<Person> personFlux = testObject.findAll();

        Mono<Person> personMono = personFlux
                .filter(person -> person.getId() == 310)
                .single();

        personMono
                .doOnError(throwable -> System.err.println(throwable.getMessage()))
                .subscribe(System.out::println);
    }
}
package com.cdi.crud.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.cdi.crud.model.Car;
import com.cdi.crud.model.Filter;
import com.cdi.crud.model.SortOrder;
import com.cdi.crud.service.CarService;

/**
 * Created by rmpestano on 9/7/14.
 */
@RunWith(Arquillian.class)
public class CrudIt extends Deployments{

    @Inject
    CarService carService;

//    @Deployment(name = "cdi-crud.war")
//    public static Archive<?> createDeployment() {
//        WebArchive war = Deployments.getBaseDeployment();
//        System.out.println(war.toString(true));
//        return war;
//    }

    @Test
    @OperateOnDeployment("it")
    public void shouldBeInitialized() {
        assertNotNull(carService);
        assertEquals(carService.crud().countAll(), 0);
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldCountCars() {
        assertEquals(carService.crud().countAll(), 4);
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldFindCarById() {
        Car car = carService.findById(1);
        assertNotNull(car);
        assertEquals(car.getId(),new Integer(1));
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldFindCarByExample() {
        Car carExample = new Car();
        carExample.setModel("Ferrari");
        Car car = carService.findByExample(carExample);
        assertNotNull(car);
        assertEquals(car.getId(),new Integer(1));
    }

    @Test
    @OperateOnDeployment("it")
    public void shouldInsertCar(){
        int countBefore = carService.count(new Filter<Car>());
        assertEquals(countBefore,0);
        Car newCar = new Car("My Car", 1);
        carService.insert(newCar);
        assertEquals(countBefore + 1, carService.count(new Filter<Car>()));
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldRemoveCar(){
        int countBefore = carService.count(new Filter<Car>());
        assertEquals(countBefore,4);
        carService.remove(new Car(1));
        assertEquals(countBefore-1, carService.count(new Filter<Car>()));
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldListCarsModel(){
        List<Car> cars = carService.listByModel("porche");
        assertNotNull(cars);
        assertEquals(cars.size(),2);
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldPaginateCars(){
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(1);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertEquals(cars.get(0).getId(),new Integer(1));
        carFilter.setFirst(1);//get second database page
        cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertEquals(cars.get(0).getId(),new Integer(2));
        carFilter.setFirst(0);
        carFilter.setPageSize(4);
        cars = carService.paginate(carFilter);
        assertEquals(cars.size(),4);
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldPaginateAndSortCars(){
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(4).setSortField("model").setSortOrder(SortOrder.DESCENDING);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(),4);
        assertTrue(cars.get(0).getModel().equals("Porche274"));
        assertTrue(cars.get(3).getModel().equals("Ferrari"));
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldPaginateCarsByModel(){
        Car carExample = new Car();
        carExample.setModel("Ferrari");
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(4).setEntity(carExample);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertTrue(cars.get(0).getModel().equals("Ferrari"));
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldPaginateCarsByPrice(){
        Car carExample = new Car();
        carExample.setPrice(12999.0);
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(2).setEntity(carExample);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertTrue(cars.get(0).getModel().equals("Mustang"));
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldPaginateCarsByIdInParam(){
        Filter<Car> carFilter = new Filter<Car>().setFirst(0).setPageSize(2).addParam("id",1);
        List<Car> cars = carService.paginate(carFilter);
        assertNotNull(cars);
        assertEquals(cars.size(), 1);
        assertTrue(cars.get(0).getId().equals(new Integer(1)));
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldListCarsByPrice(){
        List<Car> cars = carService.crud().between("price", (double) 1000, (double) 2450.9).addOrderAsc("price").list();
        //ferrari and porche
        assertNotNull(cars);
        assertEquals(cars.size(),2);
        assertEquals(cars.get(0).getModel(), "Porche");
        assertEquals(cars.get(1).getModel(), "Ferrari");
    }

    @Test
    @OperateOnDeployment("it")
    @UsingDataSet("car.yml")
    public void shouldGetCarModels(){
        List<String> models = carService.getModels("po");
        //porche and Porche274
        assertNotNull(models);
        assertEquals(models.size(),2);
        assertTrue(models.contains("Porche"));
        assertTrue(models.contains("Porche274"));
    }
}

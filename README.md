```java

        House house = randomHouse();
        Floor floor = randomFloor();
        house.setFloors(Collections.singletonList(floor));
        
        EntityController.create(House.class).put(house);
```
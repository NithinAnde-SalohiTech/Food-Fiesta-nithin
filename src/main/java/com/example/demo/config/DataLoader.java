package com.example.demo.config;

import com.example.demo.entities.Admin;
import com.example.demo.entities.Driver;
import com.example.demo.entities.Product;
import com.example.demo.entities.Restaurant;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.repositories.DriverRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.RestaurantRepository;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;

@Configuration
public class DataLoader {

    // SRID 4326 (WGS84). JTS expects POINT(longitude latitude).
    private static final GeometryFactory GEO = new GeometryFactory(new PrecisionModel(), 4326);

    private static Point point(double lon, double lat) {
        return GEO.createPoint(new Coordinate(lon, lat));
    }

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository,
                                   AdminRepository adminRepository,
                                   RestaurantRepository restaurantRepository,
                                   DriverRepository driverRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            Restaurant spice = null;
            Restaurant wok = null;
            if (restaurantRepository.count() == 0) {
                spice = new Restaurant();
                spice.setName("Spice Route Kitchen");
                spice.setAddress("MG Road, Bengaluru");
                spice.setAvgPrepMinutes(25);
                spice.setLocation(point(77.6094, 12.9757)); // lon, lat

                wok = new Restaurant();
                wok.setName("Golden Wok");
                wok.setAddress("Indiranagar, Bengaluru");
                wok.setAvgPrepMinutes(18);
                wok.setLocation(point(77.6408, 12.9719));

                restaurantRepository.saveAll(List.of(spice, wok));
                System.out.println("Sample restaurants seeded into database.");
            }

            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setPname("Hyderabadi Chicken Biryani");
                p1.setPprice(350.0);
                p1.setPdescription("Authentic long-grain basmati rice cooked with tender chicken and secret spices.");

                Product p2 = new Product();
                p2.setPname("Paneer Butter Masala");
                p2.setPprice(280.0);
                p2.setPdescription("Rich and creamy cottage cheese cubes served in a velvet tomato gravy.");

                Product p3 = new Product();
                p3.setPname("Chicken Tikka Tandoori");
                p3.setPprice(320.0);
                p3.setPdescription("Smoky charcoal-grilled chicken marinated in yogurt and traditional tandoori tikka masala.");

                Product p4 = new Product();
                p4.setPname("Veg Manchurian");
                p4.setPprice(220.0);
                p4.setPdescription("Savory vegetable balls tossed in a tangy and spicy oriental soy-based sauce.");

                Product p5 = new Product();
                p5.setPname("Gulab Jamun (2pcs)");
                p5.setPprice(120.0);
                p5.setPdescription("Warm, soft milk-solid dumplings dipped in fragrant cardamom-infused sugar syrup.");

                Product p6 = new Product();
                p6.setPname("Lucknowi Mutton Biryani");
                p6.setPprice(420.0);
                p6.setPdescription("Fragrant Lucknowi rice layered with slow-cooked spiced mutton.");

                Product p7 = new Product();
                p7.setPname("Butter Chicken");
                p7.setPprice(340.0);
                p7.setPdescription("Tender tandoori chicken cooked in a rich, buttery, spiced tomato gravy.");

                Product p8 = new Product();
                p8.setPname("Chola Bhatura");
                p8.setPprice(180.0);
                p8.setPdescription("Spicy chickpea curry served with two fluffy fried leavened flatbreads.");

                Product p9 = new Product();
                p9.setPname("Veg Hakka Noodles");
                p9.setPprice(200.0);
                p9.setPdescription("Wok-tossed noodles with colorful julienned vegetables and authentic Chinese sauces.");

                Product p10 = new Product();
                p10.setPname("Honey Chilli Potato");
                p10.setPprice(210.0);
                p10.setPdescription("Crispy fried potato fingers tossed in a sweet, spicy, and tangy honey sesame glaze.");

                Product p11 = new Product();
                p11.setPname("Traditional Rice Kheer");
                p11.setPprice(140.0);
                p11.setPdescription("Creamy slow-cooked rice pudding sweetened and flavored with saffron, cardamom, and nuts.");

                if (spice != null && wok != null) {
                    // Indian mains at Spice Route, Chinese/dessert at Golden Wok.
                    for (Product p : List.of(p1, p2, p3, p5, p6, p7, p8, p11)) {
                        p.setRestaurant(spice);
                    }
                    for (Product p : List.of(p4, p9, p10)) {
                        p.setRestaurant(wok);
                    }
                }

                productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11));
                System.out.println("Sample products data seeded into database.");
            }

            if (driverRepository.count() == 0) {
                Driver d1 = new Driver();
                d1.setName("Ravi Kumar");
                d1.setPhone("9800000001");
                d1.setStatus(Driver.Status.AVAILABLE);
                d1.setLastLocation(point(77.6150, 12.9760));
                d1.setLastLocationAt(Instant.now());

                Driver d2 = new Driver();
                d2.setName("Anita Sharma");
                d2.setPhone("9800000002");
                d2.setStatus(Driver.Status.AVAILABLE);
                d2.setLastLocation(point(77.6380, 12.9700));
                d2.setLastLocationAt(Instant.now());

                driverRepository.saveAll(List.of(d1, d2));
                System.out.println("Sample drivers seeded into database.");
            }

            if (adminRepository.count() == 0) {
                Admin defaultAdmin = new Admin();
                defaultAdmin.setAdminName("Super Admin");
                defaultAdmin.setAdminEmail("admin@foodfiesta.com");
                defaultAdmin.setAdminPassword(passwordEncoder.encode("admin123"));
                defaultAdmin.setAdminNumber("9876543210");
                adminRepository.save(defaultAdmin);
                System.out.println("Default Admin created: admin@foodfiesta.com / admin123");
            }
        };
    }
}

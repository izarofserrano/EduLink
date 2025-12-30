package com.edulink.speedtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SpeedTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealthEndpointSpeed() throws Exception {
        System.out.println("\n=== Testing Health Endpoint Speed ===");
        
        long totalTime = 0;
        int requests = 10;
        
        for (int i = 0; i < requests; i++) {
            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(get("/actuator/health"))
                    .andExpect(status().isOk());
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            totalTime += duration;
            
            System.out.println("Request " + (i + 1) + ": " + duration + "ms");
        }
        
        double avgTime = (double) totalTime / requests;
        System.out.println("\nAverage response time: " + avgTime + "ms");
        System.out.println("Total time: " + totalTime + "ms");
        
        // Assert: promedio debe ser menor a 200ms
        assert avgTime < 200 : "Average response time too high: " + avgTime + "ms";
    }

    @Test
    public void testCoursesEndpointSpeed() throws Exception {
        System.out.println("\n=== Testing Courses Endpoint Speed ===");
        
        long totalTime = 0;
        int requests = 10;
        
        for (int i = 0; i < requests; i++) {
            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(get("/api/courses"))
                    .andExpect(status().isOk());
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            totalTime += duration;
            
            System.out.println("Request " + (i + 1) + ": " + duration + "ms");
        }
        
        double avgTime = (double) totalTime / requests;
        System.out.println("\nAverage response time: " + avgTime + "ms");
        System.out.println("Total time: " + totalTime + "ms");
        
        // Assert: promedio debe ser menor a 500ms
        assert avgTime < 500 : "Average response time too high: " + avgTime + "ms";
    }

    @Test
    public void testDocumentsEndpointSpeed() throws Exception {
        System.out.println("\n=== Testing Documents Endpoint Speed ===");
        
        long totalTime = 0;
        int requests = 10;
        
        for (int i = 0; i < requests; i++) {
            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(get("/api/documents"))
                    .andExpect(status().isOk());
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            totalTime += duration;
            
            System.out.println("Request " + (i + 1) + ": " + duration + "ms");
        }
        
        double avgTime = (double) totalTime / requests;
        System.out.println("\nAverage response time: " + avgTime + "ms");
        System.out.println("Total time: " + totalTime + "ms");
        
        // Assert: promedio debe ser menor a 1000ms
        assert avgTime < 1000 : "Average response time too high: " + avgTime + "ms";
    }

    @Test
    public void testActivitiesEndpointSpeed() throws Exception {
        System.out.println("\n=== Testing Activities Endpoint Speed ===");
        
        long totalTime = 0;
        int requests = 10;
        
        for (int i = 0; i < requests; i++) {
            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(get("/api/activities"))
                    .andExpect(status().isOk());
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            totalTime += duration;
            
            System.out.println("Request " + (i + 1) + ": " + duration + "ms");
        }
        
        double avgTime = (double) totalTime / requests;
        System.out.println("\nAverage response time: " + avgTime + "ms");
        System.out.println("Total time: " + totalTime + "ms");
        
        // Assert: promedio debe ser menor a 500ms
        assert avgTime < 500 : "Average response time too high: " + avgTime + "ms";
    }

    @Test
    public void testForumThreadsEndpointSpeed() throws Exception {
        System.out.println("\n=== Testing Forum Threads Endpoint Speed ===");
        
        long totalTime = 0;
        int requests = 10;
        
        for (int i = 0; i < requests; i++) {
            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(get("/api/forum/threads"))
                    .andExpect(status().isOk());
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            totalTime += duration;
            
            System.out.println("Request " + (i + 1) + ": " + duration + "ms");
        }
        
        double avgTime = (double) totalTime / requests;
        System.out.println("\nAverage response time: " + avgTime + "ms");
        System.out.println("Total time: " + totalTime + "ms");
        
        // Assert: promedio debe ser menor a 1000ms
        assert avgTime < 1000 : "Average response time too high: " + avgTime + "ms";
    }
}



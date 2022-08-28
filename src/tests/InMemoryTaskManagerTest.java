package tests;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskManager;
import service.Managers;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEachInMemory() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        super.beforeEach();
    }

}
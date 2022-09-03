package yandex;

import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskManager;
import service.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEachInMemory() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        super.beforeEach();
    }

}
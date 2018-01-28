
public class PlayerMetaMySQLControllerIT {
/*
    private static Plugin mockPlugin() {
        final YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("sql.enabled", false);
        configuration.set("sql.host", "localhost");
        configuration.set("sql.port", 3306);
        configuration.set("sql.database", "db");
        configuration.set("sql.username", "root");
        configuration.set("sql.password", "");
        final Plugin plugin = mock(Plugin.class);
        final Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(Logger.getGlobal());
        if(Bukkit.getServer() == null)
            Bukkit.setServer(server);
        new File("BlockBall/BlockBall.db").delete();
        when(plugin.getDataFolder()).thenReturn(new File("BlockBall"));
        when(plugin.getConfig()).thenReturn(configuration);
        when(plugin.getResource(any(String.class))).thenAnswer(invocationOnMock -> {
            final String file = invocationOnMock.getArgument(0);
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        });
        return plugin;
    }


    private static DB database;

    @AfterAll
    public static void stopMariaDB()
    {
        try {
            database.stop();
        } catch (final ManagedProcessException e) {
            Logger.getLogger(PlayerMetaMySQLControllerIT.class.getSimpleName()).log(Level.WARNING, "Failed to stop mariadb.", e);
        }
    }

    @BeforeAll
    public static void startMariaDB() {
        try {
            Factory.disable();
            database = DB.newEmbeddedDB(3306);
            database.start();
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root&password=")) {
                try (Statement statement = conn.createStatement()) {
                    statement.executeUpdate("CREATE DATABASE db");
                }
            }
        } catch (SQLException | ManagedProcessException e) {
            Logger.getLogger(PlayerMetaMySQLControllerIT.class.getSimpleName()).log(Level.WARNING, "Failed to start mariadb.", e);
        }
    }

    @Test
    public void insertSelectPlayerMetaTest() throws ClassNotFoundException {
        final Plugin plugin = mockPlugin();
        plugin.getConfig().set("sql.enabled", true);
        Factory.initialize(plugin);
        try (PlayerMetaController controller = Factory.createPlayerDataController()) {
            for (final PlayerMeta item : controller.getAll()) {
                controller.remove(item);
            }
            final UUID uuid = UUID.randomUUID();
            final PlayerMeta playerMeta = new PlayerData();

            assertThrows(IllegalArgumentException.class, () -> controller.store(playerMeta));
            assertEquals(0, controller.size());

            playerMeta.setUuid(uuid);
            controller.store(playerMeta);
            assertEquals(0, controller.size());

            playerMeta.setName("Sample");
            controller.store(playerMeta);
            assertEquals(1, controller.size());
            assertEquals(uuid, controller.getById(playerMeta.getId()).get().getUUID());
        } catch (final Exception e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Failed to run test.", e);
            Assert.fail();
        }
    }


    @Test
    public void storeLoadPlayerMetaTest() throws ClassNotFoundException {
        final Plugin plugin = mockPlugin();
        plugin.getConfig().set("sql.enabled", true);
        Factory.initialize(plugin);
        try (PlayerMetaController controller = Factory.createPlayerDataController()) {
            for (final PlayerMeta item : controller.getAll()) {
                controller.remove(item);
            }
            UUID uuid = UUID.randomUUID();
            PlayerMeta playerMeta = new PlayerData();
            playerMeta.setName("Second");
            playerMeta.setUuid(uuid);
            controller.store(playerMeta);

            assertEquals(1, controller.size());
            playerMeta = controller.getAll().get(0);
            assertEquals(uuid, playerMeta.getUUID());
            assertEquals("Second", playerMeta.getName());

            uuid = UUID.randomUUID();
            playerMeta.setName("Shynixn");
            playerMeta.setUuid(uuid);
            controller.store(playerMeta);

            playerMeta = controller.getAll().get(0);
            assertEquals(uuid, playerMeta.getUUID());
            assertEquals("Shynixn", playerMeta.getName());
        } catch (final Exception e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Failed to run test.", e);
            Assert.fail();
        }
    }*/
}

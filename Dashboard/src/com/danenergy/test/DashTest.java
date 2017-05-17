package com.danenergy.test;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.TimeSection;
import eu.hansolo.tilesfx.skins.BarChartItem;
import javafx.application.Application;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;


public class DashTest extends Application {

    private static final Color DAN_TECH_LIGHTGREEN = Color.rgb(168, 201, 75);
    private static final Color DAN_TECH_GREEN = Color.rgb(121, 182, 82);
    private static final Color DAN_TECH_DARKGREEN = Color.rgb(31, 111, 59);

    private static final double TILE_SIZE = 250;
    private Tile            currentTile;
    private Tile            voltageTile;
    private Gauge bulletChartGauge;
    private Tile  temperatureTile;

    private Gauge temperatureGauge;
    private Tile  bulletChartTile;

    private Gauge stateOfChargeGauge;
    private Tile stateOfChargeTile;

    private Gauge statusGauge;
    private Tile statusTile;

    private Button btn;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();

        currentTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.GAUGE)
                .title("Current")
                .unit("Amp")
                .thresholdVisible(false)
                .barColor(DAN_TECH_GREEN)
                .minValue(-100)
                .maxValue(100)

                .build();


        voltageTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.GAUGE)
                .title("Voltage")
                .unit("V")
                .thresholdVisible(false)
                .barColor(DAN_TECH_GREEN)
                .minValue(0)
                .maxValue(70)
                .build();

        temperatureGauge = createGauge(Gauge.SkinType.SIMPLE_SECTION);
        temperatureGauge.setBarColor(Tile.FOREGROUND);
        temperatureGauge.setMinValue(-20);
        temperatureGauge.setMaxValue(60);
        temperatureGauge.setSections(
                new Section(-20,0,Tile.BLUE.brighter()),
                new Section(45,60,Tile.RED.brighter()));

        temperatureTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .title("Temperature")
                .graphic(temperatureGauge)
                .build();

        stateOfChargeGauge = createGauge(Gauge.SkinType.LEVEL);
        stateOfChargeGauge.setBarColor(DAN_TECH_GREEN);
        stateOfChargeGauge.setMinValue(0);
        stateOfChargeGauge.setMaxValue(100);

        stateOfChargeTile = TileBuilder.create()
                .prefSize(TILE_SIZE,TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .title("State Of Charge")
                .graphic(stateOfChargeGauge)
                .build();

        statusGauge = createGauge(Gauge.SkinType.TINY);
        statusGauge.setBarColor(Tile.FOREGROUND);
        statusGauge.setMinValue(0);
        statusGauge.setMaxValue(6);
        statusGauge.setSections(
                new Section(0,2,DAN_TECH_GREEN),
                new Section(2,4,Tile.ORANGE),
                new Section(4,6,Tile.RED));
        statusGauge.setNeedleColor(Tile.BACKGROUND);
        //statusGauge.setNeedleColor(Tile.DARK_BLUE);

        statusTile = TileBuilder.create()
                .prefSize(TILE_SIZE,TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .title("Status")
                .graphic(statusGauge)
                .build();

        bulletChartGauge = createGauge(Gauge.SkinType.BULLET_CHART);
        bulletChartGauge.setThreshold(75);
        bulletChartGauge.setBarColor(Tile.FOREGROUND);
        bulletChartGauge.setThresholdColor(Tile.RED);
        bulletChartGauge.setSections(new Section(0, 33, Tile.BLUE.darker().darker()),
                new Section(33, 66, Tile.BLUE.darker()),
                new Section(66, 100, Tile.BLUE));
        bulletChartTile = TileBuilder.create()
                .prefSize(2 * TILE_SIZE + 10, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .title("Medusa BulletChart")
                .text("Temperature")
                .graphic(bulletChartGauge)
                .build();

        btn = new Button("Update Gauge");
        btn.setOnAction(event -> {

            currentTile.setValue(10.0);

            voltageTile.setValue(5.5);

            temperatureGauge.setValue(-10);

            stateOfChargeGauge.setValue(50);

            int x= 3;

            statusGauge.setValue(2*x -1);


        });
    }

    @Override
    public void start(Stage primaryStage) {
        FlowPane pane = new FlowPane(Orientation.HORIZONTAL,5,5,currentTile,voltageTile,temperatureTile,
                stateOfChargeTile,statusTile,btn);
        pane.setPadding(new Insets(5));
        pane.setPrefSize(800,  480);
        pane.setBackground(new Background(new BackgroundFill(Tile.BACKGROUND.darker(), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        primaryStage.setTitle("TilesFX Dashboard using Medusa");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private Gauge createGauge(final Gauge.SkinType TYPE) {
        return GaugeBuilder.create()
                .skinType(TYPE)
                .prefSize(TILE_SIZE, TILE_SIZE)
                .animated(true)
                        //.title("")
                .unit("\u00B0C")
                .valueColor(Tile.FOREGROUND)
                .titleColor(Tile.FOREGROUND)
                .unitColor(Tile.FOREGROUND)
                .barColor(Tile.BLUE)
                .needleColor(Tile.FOREGROUND)
                .barColor(Tile.BLUE)
                .barBackgroundColor(Tile.BACKGROUND.darker())
                .tickLabelColor(Tile.FOREGROUND)
                .majorTickMarkColor(Tile.FOREGROUND)
                .minorTickMarkColor(Tile.FOREGROUND)
                .mediumTickMarkColor(Tile.FOREGROUND)
                .build();
    }
}

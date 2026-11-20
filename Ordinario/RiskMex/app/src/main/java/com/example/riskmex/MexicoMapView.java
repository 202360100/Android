package com.example.riskmex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.example.riskmex.model.Territory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MexicoMapView extends View {

    public interface OnTerritorySelectedListener {
        void onTerritorySelected(Territory territory);
    }

    private static class StateShape {
        Territory territory;
        List<List<PointF>> polygons = new ArrayList<>();

        StateShape(Territory territory) {
            this.territory = territory;
        }
    }

    private final List<StateShape> shapes = new ArrayList<>();

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectedBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectedOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private OnTerritorySelectedListener listener;
    private Territory selectedTerritory;

    private double minLon = Double.MAX_VALUE;
    private double maxLon = -Double.MAX_VALUE;
    private double minLat = Double.MAX_VALUE;
    private double maxLat = -Double.MAX_VALUE;

    private float scale = 1f;
    private float offsetX;
    private float offsetY;

    private final RectF mapBounds = new RectF();

    public MexicoMapView(Context context, List<Territory> territories) {
        super(context);

        fillPaint.setStyle(Paint.Style.FILL);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.WHITE);

        selectedBorderPaint.setStyle(Paint.Style.STROKE);
        selectedBorderPaint.setColor(0xFFFFD54F);

        selectedOutlinePaint.setStyle(Paint.Style.STROKE);
        selectedOutlinePaint.setColor(Color.BLACK);

        labelPaint.setColor(Color.WHITE);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

        labelOutlinePaint.setColor(Color.BLACK);
        labelOutlinePaint.setTextAlign(Paint.Align.CENTER);
        labelOutlinePaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        labelOutlinePaint.setStyle(Paint.Style.STROKE);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        loadGeoJson(territories);
    }

    public void setOnTerritorySelectedListener(
            OnTerritorySelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedTerritory(Territory territory) {
        selectedTerritory = territory;
        invalidate();
    }

    private void loadGeoJson(List<Territory> territories) {

        try {
            InputStream input = getContext().getAssets()
                    .open("states_simple.geojson");

            byte[] bytes = input.readAllBytes();
            input.close();

            String jsonText = new String(bytes, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(jsonText);
            JSONArray features = root.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {

                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");

                String stateName = properties.getString("NOMGEO");

                Territory territory = findTerritory(
                        territories, stateName);

                if (territory == null) continue;

                StateShape shape = new StateShape(territory);

                JSONObject geometry = feature.getJSONObject("geometry");
                String type = geometry.getString("type");
                JSONArray coordinates = geometry.getJSONArray("coordinates");

                if (type.equals("Polygon")) {
                    addPolygon(shape, coordinates);
                } else if (type.equals("MultiPolygon")) {

                    for (int j = 0; j < coordinates.length(); j++) {
                        addPolygon(shape, coordinates.getJSONArray(j));
                    }
                }

                shapes.add(shape);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        invalidate();
    }

    private Territory findTerritory(
            List<Territory> territories, String geoName) {

        String normalized = normalize(geoName);

        for (Territory territory : territories) {

            String gameName = normalize(territory.getName());

            if (normalized.equals(gameName)) {
                return territory;
            }
        }

        if (normalized.equals("mexico")) {
            return findByName(territories, "Estado de México");
        }

        if (normalized.equals("coahuila de zaragoza")) {
            return findByName(territories, "Coahuila");
        }

        if (normalized.equals("michoacan de ocampo")) {
            return findByName(territories, "Michoacán");
        }

        if (normalized.equals("veracruz de ignacio de la llave")) {
            return findByName(territories, "Veracruz");
        }

        return null;
    }

    private Territory findByName(
            List<Territory> territories, String name) {

        for (Territory territory : territories) {
            if (territory.getName().equals(name)) {
                return territory;
            }
        }

        return null;
    }

    private String normalize(String text) {
        return java.text.Normalizer.normalize(
                        text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }

    private void addPolygon(StateShape shape, JSONArray rings)
            throws Exception {

        if (rings.length() == 0) return;

        JSONArray outerRing = rings.getJSONArray(0);

        List<PointF> points = new ArrayList<>();

        for (int i = 0; i < outerRing.length(); i++) {

            JSONArray coordinate = outerRing.getJSONArray(i);

            double lon = coordinate.getDouble(0);
            double lat = coordinate.getDouble(1);

            minLon = Math.min(minLon, lon);
            maxLon = Math.max(maxLon, lon);

            minLat = Math.min(minLat, lat);
            maxLat = Math.max(maxLat, lat);

            points.add(new PointF((float) lon, (float) lat));
        }

        shape.polygons.add(points);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (shapes.isEmpty()) {
            fillPaint.setColor(Color.WHITE);
            fillPaint.setTextSize(35);
            canvas.drawText(
                    "No se pudo cargar el mapa",
                    30, 80, fillPaint);
            return;
        }

        calculateTransform();

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale * 0.85f, -scale);

        // Primera pasada: dibujar todos los estados y sus fronteras.
        for (StateShape shape : shapes) {

            Territory territory = shape.territory;

            int color = territory.getOwner() != null
                    ? territory.getOwner().getColor()
                    : Color.GRAY;

            fillPaint.setColor(color);

            for (List<PointF> polygon : shape.polygons) {

                Path path = createPath(polygon);

                canvas.drawPath(path, fillPaint);

                borderPaint.setStrokeWidth(1.5f / scale);
                canvas.drawPath(path, borderPaint);
            }
        }

        // Segunda pasada: dibujar el estado seleccionado por encima.
        if (selectedTerritory != null) {

            selectedOutlinePaint.setStrokeWidth(5f / scale);
            selectedBorderPaint.setStrokeWidth(3f / scale);

            for (StateShape shape : shapes) {

                if (shape.territory != selectedTerritory) continue;

                for (List<PointF> polygon : shape.polygons) {

                    Path path = createPath(polygon);

                    // Contorno oscuro exterior.
                    canvas.drawPath(path, selectedOutlinePaint);

                    // Borde dorado interior.
                    canvas.drawPath(path, selectedBorderPaint);
                }

                break;
            }
        }

        canvas.restore();

        if (selectedTerritory != null) {
            drawSelectedStateLabel(canvas);
        }
    }

    private void calculateTransform() {

        if (maxLon <= minLon || maxLat <= minLat) return;

        float padding = 25f;

        float availableWidth = getWidth() - padding * 2;
        float availableHeight = getHeight() - padding * 2;

        float mapWidth = (float) (maxLon - minLon);
        float mapHeight = (float) (maxLat - minLat);

        float adjustedWidth = mapWidth * 0.85f;

        scale = Math.min(
                availableWidth / adjustedWidth,
                availableHeight / mapHeight
        );

        float drawnWidth = adjustedWidth * scale;
        float drawnHeight = mapHeight * scale;

        offsetX = (getWidth() - drawnWidth) / 2f
                - (float) minLon * 0.85f * scale;

        offsetY = (getHeight() - drawnHeight) / 2f
                + (float) maxLat * scale;

        mapBounds.set(
                padding,
                padding,
                getWidth() - padding,
                getHeight() - padding
        );
    }

    private Path createPath(List<PointF> points) {

        Path path = new Path();

        if (points.isEmpty()) return path;

        path.moveTo(points.get(0).x, points.get(0).y);

        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }

        path.close();

        return path;
    }

    private void drawSelectedStateLabel(Canvas canvas) {

        if (scale <= 0) return;

        float textSize = Math.max(11f, Math.min(19f, scale * 0.65f));

        labelPaint.setTextSize(textSize);
        labelOutlinePaint.setTextSize(textSize);

        labelOutlinePaint.setStrokeWidth(3f);

        for (StateShape shape : shapes) {

            if (shape.territory != selectedTerritory) continue;

            List<PointF> largestPolygon = null;

            for (List<PointF> polygon : shape.polygons) {
                if (largestPolygon == null ||
                        polygon.size() > largestPolygon.size()) {
                    largestPolygon = polygon;
                }
            }

            if (largestPolygon == null || largestPolygon.isEmpty()) continue;

            float centerLon = 0;
            float centerLat = 0;

            for (PointF point : largestPolygon) {
                centerLon += point.x;
                centerLat += point.y;
            }

            centerLon /= largestPolygon.size();
            centerLat /= largestPolygon.size();

            float screenX = offsetX + centerLon * 0.85f * scale;
            float screenY = offsetY - centerLat * scale;

            String abbreviation = getStateAbbreviation(
                    shape.territory.getName());

            String troopCount = String.valueOf(
                    shape.territory.getArmy().getTotalUnits());

            float lineHeight = textSize * 1.15f;

            canvas.drawText(
                    abbreviation, screenX, screenY, labelOutlinePaint);

            canvas.drawText(
                    abbreviation, screenX, screenY, labelPaint);

            float troopY = screenY + lineHeight;

            canvas.drawText(
                    troopCount, screenX, troopY, labelOutlinePaint);

            canvas.drawText(
                    troopCount, screenX, troopY, labelPaint);

            break;
        }
    }

    private String getStateAbbreviation(String name) {

        switch (name) {
            case "Aguascalientes": return "AGS";
            case "Baja California": return "BC";
            case "Baja California Sur": return "BCS";
            case "Campeche": return "CAM";
            case "Chiapas": return "CHIS";
            case "Chihuahua": return "CHIH";
            case "Ciudad de México": return "CDMX";
            case "Coahuila": return "COAH";
            case "Colima": return "COL";
            case "Durango": return "DGO";
            case "Estado de México": return "MEX";
            case "Guanajuato": return "GTO";
            case "Guerrero": return "GRO";
            case "Hidalgo": return "HGO";
            case "Jalisco": return "JAL";
            case "Michoacán": return "MICH";
            case "Morelos": return "MOR";
            case "Nayarit": return "NAY";
            case "Nuevo León": return "NL";
            case "Oaxaca": return "OAX";
            case "Puebla": return "PUE";
            case "Querétaro": return "QRO";
            case "Quintana Roo": return "QROO";
            case "San Luis Potosí": return "SLP";
            case "Sinaloa": return "SIN";
            case "Sonora": return "SON";
            case "Tabasco": return "TAB";
            case "Tamaulipas": return "TAM";
            case "Tlaxcala": return "TLAX";
            case "Veracruz": return "VER";
            case "Yucatán": return "YUC";
            case "Zacatecas": return "ZAC";
            default: return name.substring(
                    0, Math.min(3, name.length())
            ).toUpperCase();
        }
    }

    private boolean containsPoint(
            List<PointF> polygon, float x, float y) {

        boolean inside = false;

        int j = polygon.size() - 1;

        for (int i = 0; i < polygon.size(); i++) {

            PointF a = polygon.get(i);
            PointF b = polygon.get(j);

            boolean intersects =
                    ((a.y > y) != (b.y > y))
                            && (x < (b.x - a.x) * (y - a.y)
                            / (b.y - a.y) + a.x);

            if (intersects) {
                inside = !inside;
            }

            j = i;
        }

        return inside;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (scale <= 0) return true;

            float lon = (event.getX() - offsetX) / (scale * 0.85f);
            float lat = (offsetY - event.getY()) / scale;

            for (int i = shapes.size() - 1; i >= 0; i--) {

                StateShape shape = shapes.get(i);

                for (List<PointF> polygon : shape.polygons) {

                    if (containsPoint(polygon, lon, lat)) {

                        selectedTerritory = shape.territory;
                        invalidate();

                        if (listener != null) {
                            listener.onTerritorySelected(
                                    shape.territory);
                        }

                        return true;
                    }
                }
            }

            return true;
        }

        return true;
    }
}

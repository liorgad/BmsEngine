package com.danenergy.common.parser;

import com.danenergy.common.Pair;
import com.danenergy.common.ParserDefinition;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.*;

/**
 * Created by Lior Gad on 2/13/2017.
 */

public class GenericParser{
    //logging
    //final static Logger logger = Logger.getLogger(GenericParser.class);
    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    public static Map<String,List<Pair<String, ParserDefinition>>> typeMap = new HashMap<>(0);

    private static <T extends Object> void SetObject(T newObject, Field objectField, ParserDefinition value, Map<String, String> map) throws IllegalAccessException {
        if (objectField.getType() == (Byte.TYPE)) {
            objectField.setByte(newObject, Byte.parseByte(map.get(objectField.getName()), 16));
        } else if (objectField.getType() == (Short.TYPE)) {
            objectField.setShort(newObject, Short.parseShort(map.get(objectField.getName()), 16));
        } else if (objectField.getType() == (Integer.TYPE)) {
            if (value.IsSigned()) {
                objectField.setInt(newObject, Integer.parseInt(map.get(objectField.getName()), 16));
            } else {
                objectField.setInt(newObject, Integer.parseUnsignedInt(map.get(objectField.getName()), 16));
            }
        } else if (objectField.getType() == (Long.TYPE)) {
            if (value.IsSigned()) {
                objectField.setLong(newObject, Long.parseUnsignedLong(map.get(objectField.getName()), 16));
            } else {
                objectField.setLong(newObject, Long.parseLong(map.get(objectField.getName()), 16));
            }
        } else if (objectField.getType() == (String.class)) {
            objectField.set(newObject, map.get(objectField.getName()));
        } else if (objectField.getType() == (short[].class)) {
            String strData = map.get(objectField.getName());
            int[] intArr = java.util.stream.IntStream.range(0, strData.length() / 2).
                    map(v -> Integer.parseInt(strData.substring(v * 2, (v * 2) + 2), 16)).toArray();
            short[] shortArr = new short[intArr.length];
            for (int i = 0; i < intArr.length; i++) {
                shortArr[i] = (short) intArr[i];
            }

            objectField.set(newObject, shortArr);
        } else if (objectField.getType() == (int[].class)) {
            String strData = map.get(objectField.getName());
            int[] intArr = java.util.stream.IntStream.range(0, strData.length() / 4).
                    map(v -> Integer.parseUnsignedInt(strData.substring(v * 4, ((v * 4) + 4)), 16)).toArray();
            objectField.set(newObject, intArr);
        }
    }

    public static <T extends Object> T Parse(String data, Class<T> type) {
        try {
            if (!typeMap.containsKey(type.getName())) {

                typeMap.put(type.getName(),ExtractAndOrderFields(type));
            }

            List<Pair<String, ParserDefinition>> orderedList = typeMap.get(type.getName());

//            for (Pair<String, ParserDefinition> p : orderedList) {
//                logger.info(p.getKey() + " : " + p.getValue().Index() + "," + p.getValue().BytesLength());
//            }

            Map<String, String> map = ParseASCIIHex(data, orderedList);

            T newObject = type.newInstance();

            for (Pair<String, ParserDefinition> p : orderedList) {
                Field objectField = type.getDeclaredField(p.getKey());

                SetObject(newObject, objectField, p.getValue(), map);
            }

            return newObject;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return null;
    }

    public static <T extends Object> T ParseFromBytes(byte[] data, Class<T> type) {
        try {
            if (!typeMap.containsKey(type.getName())) {

                typeMap.put(type.getName(),ExtractAndOrderFields(type));
            }

            List<Pair<String, ParserDefinition>> orderedList = typeMap.get(type.getName());

//            for (Pair<String, ParserDefinition> p : orderedList) {
//                logger.info(p.getKey() + " : " + p.getValue().Index() + "," + p.getValue().BytesLength());
//            }

            Map<String, byte[]> map = ParseBytes(data, orderedList);

            T newObject = type.newInstance();

            for (Pair<String, ParserDefinition> p : orderedList) {
                Field objectField = type.getDeclaredField(p.getKey());

                SetObjectBytes(newObject, objectField, p.getValue(), map);
            }

            return newObject;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return null;
    }

    private static Map<String, byte[]> ParseBytes(byte[] data, List<Pair<String, ParserDefinition>> orderedList) {
        int index = 0;
        int totalKnownLength = orderedList.stream().
                filter(p -> p.getValue().BytesLength() != 0).
                mapToInt(p -> p.getValue().BytesLength()).sum();


        Map<String, byte[]> fieldToBytesMap = new HashMap<>();

        for (Pair<String, ParserDefinition> pair : orderedList) {

            int fieldLength = pair.getValue().BytesLength();

            if(fieldLength == 0 && StringUtils.isNotEmpty(pair.getValue().RelatedFieldLength()))
            {
                String lengthField = pair.getValue().RelatedFieldLength();

                byte[] fieldLengthBytes = fieldToBytesMap.get(lengthField);

                ByteBuffer wrapped = ByteBuffer.wrap(fieldLengthBytes); // big-endian by default

                switch (fieldLengthBytes.length)
                {
                    case 1:
                        fieldLength = wrapped.get();
                        break;
                    case 2:
                        fieldLength = wrapped.getShort();
                        break;
                    case 4:
                        fieldLength = wrapped.getInt();
                        break;
                    default:
                        fieldLength = wrapped.getInt();
                }
            }

            fieldToBytesMap.put(pair.getKey(), ArrayUtils.subarray(data, index, index + fieldLength));// ArrayUtils.subarray(data, index, index + fieldLength));

            index += fieldLength;

        }
        return fieldToBytesMap;
    }

    private static Map<String, String> ParseASCIIHex(String data, List<Pair<String, ParserDefinition>> orderedList) {
        int index = 0;
        int totalKnownLength = orderedList.stream().
                filter(p -> p.getValue().ASCIILength() != 0).
                mapToInt(p -> p.getValue().ASCIILength()).sum();

        Map<String, String> fieldToBytesMap = new HashMap<>();

        for (Pair<String, ParserDefinition> pair : orderedList) {
            String lengthField = pair.getValue().RelatedFieldLength();
            int fieldLength = pair.getValue().ASCIILength();

            if (StringUtils.isNotEmpty(lengthField)) {
                if (fieldLength != 0) {
                    String l = fieldToBytesMap.get(lengthField);

                    int parsedLength = Integer.parseUnsignedInt(l, 16);

                    fieldLength *= parsedLength;
                } else {
                    String l = fieldToBytesMap.get(lengthField);

                    int parsedLength = Integer.parseUnsignedInt(l, 16);

                    fieldLength = parsedLength - totalKnownLength;
                }
            }

            if (index + fieldLength > (data.length())) {
                return null;
            }

            fieldToBytesMap.put(pair.getKey(), StringUtils.substring(data, index, index + fieldLength));// ArrayUtils.subarray(data, index, index + fieldLength));

            index += fieldLength;
        }
        return fieldToBytesMap;
    }

    public static List<Pair<String, ParserDefinition>> ExtractAndOrderFields(Class type) {
        List<Pair<String, ParserDefinition>> list = new LinkedList<>();

        for (Field f : type.getFields()) {
            list.add(new Pair<>(f.getName(), f.getAnnotation(ParserDefinition.class)));
        }

        Collections.sort(list, new Comparator<Pair<String, ParserDefinition>>() {
            public int compare(Pair<String, ParserDefinition> p1, Pair<String, ParserDefinition> p2) {

                final int BEFORE = -1;
                final int EQUAL = 0;
                final int AFTER = 1;

                //this optimization is usually worthwhile, and can
                //always be added
                if (p1.getValue().Index() == p2.getValue().Index()) return EQUAL;

                //primitive numbers follow this form
                if (p1.getValue().Index() < p2.getValue().Index()) return BEFORE;

                return AFTER;
            }
        });

        return list;
    }

    public static <T extends Object> String Build(T obj, Class type) {
        try {
            if (!typeMap.containsKey(type.getName())) {

                typeMap.put(type.getName(),ExtractAndOrderFields(type));
            }

            List<Pair<String, ParserDefinition>> orderedList = typeMap.get(type.getName());

            List<String> resultList = new LinkedList<String>();

            for (Pair<String, ParserDefinition> item : orderedList)
            {
                Field objectField = type.getDeclaredField(item.getKey());

                Object val = objectField.get(obj);
                Class fieldType = objectField.getType();

                if (fieldType == Character.TYPE) {
                    String str = new String(new char[]{(char) val});
                    resultList.add(str);
                } else if (fieldType == Byte.TYPE) {
                    String str = String.format("%02x", (byte) val);
                    resultList.add(str);
                } else if (fieldType == Short.TYPE) {
                    String str = String.format("%02x", (short) val);
                    resultList.add(str);
                } else if (fieldType == Integer.TYPE) {
                    String str = String.format("%04x", (int) val);
                    resultList.add(str);
                } else if (fieldType == String.class) {
                    String str = val != null ? (String) val : "";
                    resultList.add(str);
                } else if (fieldType == Long.TYPE) {
                    String format = "%0" + item.getValue().ASCIILength() + "x";
                    String str = String.format(format, (long) val);
                    resultList.add(str);
                } else if (fieldType == int[].class) {
                    String arrAsStr = one.util.streamex.IntStreamEx.of((int[]) val).mapToObj(v -> String.format("%04x", v)).reduce((v1, v2) -> String.format("%s%s", v1, v2)).get();


                    resultList.add(arrAsStr);
                } else if (fieldType == short[].class) {
                    String arrAsStr = one.util.streamex.IntStreamEx.of((short[]) val).mapToObj(v -> String.format("%02x", v)).reduce((v1, v2) -> String.format("%s%s", v1, v2)).get();


                    resultList.add(arrAsStr);
                }
            }

            String res = resultList.stream().reduce((s1, s2) -> s1 + s2).get().toUpperCase();
            return res;

        } catch (Exception e) {
//            logger.Error(e, "Error Building from object of type " + obj.GetType().Name);
        }

        return null;
    }

    public static <T extends Object> byte[] BuildToBytes(T obj, Class type,String[] fieldNamesToDeffer) {
        try {
            if (!typeMap.containsKey(type.getName())) {

                typeMap.put(type.getName(),ExtractAndOrderFields(type));
            }

            HashMap<String, Boolean> defferDictionary = null;
            if(fieldNamesToDeffer != null) {
                defferDictionary = new HashMap<>(fieldNamesToDeffer.length);

                for (String name : fieldNamesToDeffer) {
                    defferDictionary.put(name, true);
                }
            }
            List<Pair<String, ParserDefinition>> orderedList = typeMap.get(type.getName());

            List<byte[]> resultList = new LinkedList<byte[]>();

            for (Pair<String, ParserDefinition> item : orderedList)
            {
                Field objectField = type.getDeclaredField(item.getKey());

                if(null != defferDictionary && defferDictionary.containsKey(objectField.getName()))
                {
                    continue;
                }

                Object val = objectField.get(obj);
                Class fieldType = objectField.getType();

                if (fieldType == Character.TYPE) {
                    byte[] res = com.danenergy.common.ArrayUtils.toByteArray((char)val);
                    resultList.add(res);
                } else if (fieldType == Byte.TYPE) {
                    byte[] res = new byte[]{(byte) val};
                    resultList.add(res);
                } else if (fieldType == Short.TYPE) {
                    byte[] res = com.danenergy.common.ArrayUtils.toByteArray((short)val);
                    resultList.add(res);
                } else if (fieldType == Integer.TYPE) {
                    byte[] res = com.danenergy.common.ArrayUtils.toByteArray((int)val);
                    resultList.add(res);
                } else if (fieldType == String.class) {
                    byte[] res = ((String)val).getBytes();
                    resultList.add(res);
                } else if (fieldType == Long.TYPE) {
                    byte[] res = com.danenergy.common.ArrayUtils.toByteArray((long) val);
                    resultList.add(res);
                } else if (fieldType == int[].class) {

                    ByteBuffer byteBuffer = ByteBuffer.allocate(((int[])val).length * 4);
                    IntBuffer intBuffer = byteBuffer.asIntBuffer();
                    intBuffer.put((int[])val);

                    byte[] res =  byteBuffer.array();
                    resultList.add(res);
                } else if (fieldType == short[].class) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(((int[])val).length * 2);
                    ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
                    shortBuffer.put((short[])val);
                    byte[] res =  byteBuffer.array();
                    resultList.add(res);
                }
            }

            byte[] res = resultList.stream().reduce((s1, s2) -> ArrayUtils.addAll(s1, s2)).get();
            return res;

        } catch (Exception e) {
//            logger.Error(e, "Error Building from object of type " + obj.GetType().Name);
        }

        return null;
    }

    private static <T extends Object> void SetObjectBytes(T newObject, Field objectField, ParserDefinition value, Map<String, byte[]> map) throws IllegalAccessException {

        byte[] arr = map.get(objectField.getName());
        ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default

        if (objectField.getType() == (Byte.TYPE)) {
            objectField.setByte(newObject, wrapped.get());
        } else if (objectField.getType() == (Short.TYPE)) {
            objectField.setShort(newObject, wrapped.getShort());
        } else if (objectField.getType() == (Integer.TYPE)) {
            objectField.setInt(newObject, wrapped.getInt());
        } else if (objectField.getType() == (Long.TYPE)) {
            objectField.setLong(newObject, wrapped.getLong());
        } else if (objectField.getType() == (String.class)) {
            objectField.set(newObject, map.get(objectField.getName()));
        } else if (objectField.getType().isArray())
        {
            if(objectField.getType() == (byte[].class))
            {
                objectField.set(newObject,map.get(objectField.getName()));
            }
        }
    }
}









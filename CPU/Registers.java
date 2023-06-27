package CPU;

import java.util.HashMap;
import java.util.Map;

public class Registers {
    public Registers() {
        this._registersMap = new HashMap<String, Byte[]>();
        _registersMap.put("AYB", new Byte[] { 0b000, 0 });
        _registersMap.put("BEN", new Byte[] { 0b001, 0 });
        _registersMap.put("GIM", new Byte[] { 0b010, 0 });
        _registersMap.put("DA", new Byte[] { 0b011, 0 });
        _registersMap.put("ECH", new Byte[] { 0b100, 0 });
        _registersMap.put("ZA", new Byte[] { 0b101, 0 });
        _registersMap.put("GH", new Byte[] { 0b111, 0 });

    }

    public boolean is_register(String name) {
        return _registersMap.keySet().contains(name.toUpperCase());
    }

    public boolean is_register(byte code) {
        for (Map.Entry<String, Byte[]> entry : _registersMap.entrySet()) {
            Byte[] value = entry.getValue();

            if (value[0] == code) {
                return true;
            }
        }
        return false;
    }

    public void set_register_value(byte code, int val) {
        if (!is_register(code)) {
            throw new Error("code isn't register", null);
        }

        String key = get_register_key(code);

        Byte[] ans = _registersMap.get(key);
        ans[1] = (byte) val;
        _registersMap.put(key, ans);

    }

    public void set_register_value(String key, int val) {
        if (!is_register(key)) {
            throw new Error("code isn't register", null);
        }

        Byte[] ans = _registersMap.get(key);
        ans[1] = (byte) val;
        _registersMap.put(key, ans);

    }

    public byte get_register_code(String name) {
        if (is_register(name)) {
            return _registersMap.get(name.toUpperCase())[0];
        }
        throw new Error("Name isn't register", null);
    }

    public byte get_register_value(byte code) {
        for (Map.Entry<String, Byte[]> entry : _registersMap.entrySet()) {
            Byte[] value = entry.getValue();

            if (value[0] == code) {
                return value[1];
            }
        }
        throw new Error("this code isn't register code", null);
    }

    public byte get_register_value(String name) {
        name = name.toUpperCase();
        if (!_registersMap.keySet().contains(name)) {
            throw new Error("Name isn't register", null);
        }
        return _registersMap.get(name)[1];
    }

    public String get_register_key(byte code) {

        for (Map.Entry<String, Byte[]> entry : _registersMap.entrySet()) {

            if (entry.getValue()[0] == code) {
                return entry.getKey();
            }

        }
        return "";
    }

    public void set_instruction_pointer(int val) {
        set_register_value("GH", val);
    }

    public byte get_instruction_pointer() {
        return get_register_value("GH");
    }

    public void print_reg() {
        for (Map.Entry<String, Byte[]> entry : _registersMap.entrySet()) {
            System.out.println(entry.getKey() + ": val  " + entry.getValue()[1]);
        }
    }

    private Map<String, Byte[]> _registersMap;

}

package CPU;

import java.util.HashMap;
import java.util.Map;

public class InstrucionCodes {
    public InstrucionCodes() {
        this._instructionCode = new HashMap<String, Byte[]>();

        _instructionCode.put("MOV", new Byte[] { 0, 0b1011, 0b1100, 0b1101, 0b1110, 0b1111 });
        _instructionCode.put("ADD", new Byte[] { 2, 0b0000 });
        _instructionCode.put("SUB", new Byte[] { 2, 0b0001 });
        _instructionCode.put("MUL", new Byte[] { 2, 0b0010 });
        _instructionCode.put("DIV", new Byte[] { 2, 0b0011 });
        _instructionCode.put("AND", new Byte[] { 2, 0b0100 });
        _instructionCode.put("OR", new Byte[] { 2, 0b0101 });
        _instructionCode.put("NOT", new Byte[] { 1, 0b0110 });
        _instructionCode.put("XOR", new Byte[] { 2, 0b0111 });
        _instructionCode.put("CMP", new Byte[] { 2, 0b1000 });
        _instructionCode.put("JMP", new Byte[] { 9, 0b1001, 0b100100 });
        _instructionCode.put("JG", new Byte[] { 9, 0b1001, 0b100101 });
        _instructionCode.put("JL", new Byte[] { 9, 0b1001, 0b100110 });
        _instructionCode.put("JE", new Byte[] { 9, 0b1001, 0b100111 });
    }

    public byte get_instruction_code(byte upCode) {
        return (byte) ((upCode >> 4) & 0b00001111);
    }

    public byte get_instruction_type(byte instCode) {
        for (Map.Entry<String, Byte[]> entry : _instructionCode.entrySet()) {

            Byte[] value = entry.getValue();

            for (int i = 1; i < value.length; ++i) {
                if (value[i] == instCode) {
                    return value[0];
                }
            }
        }
        return -1;
    }

    public Byte[] get_instruction_info(String instName) {

        return _instructionCode.get(instName);
    }

    public String get_jumps_key(byte upCode) {
        return get_instruction_key((byte) ((upCode & 0b11111100) >> 2));
    }

    public String get_instruction_key(byte instCode) {

        for (Map.Entry<String, Byte[]> entry : _instructionCode.entrySet()) {
            Byte[] value = entry.getValue();

            for (int i = 1; i < value.length; ++i) {
                if (value[i] == instCode) {
                    return entry.getKey();
                }
            }
        }
        return "";
    }

    private Map<String, Byte[]> _instructionCode;

}

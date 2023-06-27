import CPU.CPU;
import CPU.ParserInstruction.ParserInstruction;
import CPU.InstrucionCodes;
import CPU.Registers;

class main {
    public static void main(String[] args) {

        if (args.length == 0) {
            throw new Error("ERROR: input file name when running main");
        }

        CPU intel = new CPU();

        intel.execute_program(args[0]);
        intel.dump_free_memory();
    }

}
import CPU.CPU;
import CPU.ParserInstruction.ParserInstruction;
import CPU.InstrucionCodes;
import CPU.Registers.Registers;

class main {
    public static void main(String[] args) {

        if (args.length == 0) {
            throw new Error("ERROR: input file name when running main");
        }
        CPU intel = new CPU();

        if (args.length == 2 && args[1].equals("debugger")) {

            intel.execute_program(args[0], "debugger");

        } else {
            // intel.execute_program(args[0], "debugger");
            intel.execute_program(args[0], "");
            intel.dump_memory();
            // intel.dump_free_memory();
            intel.print_stack();
            intel.print_registers_value();
        }

    }

}
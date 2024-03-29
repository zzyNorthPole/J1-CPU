package j1cpu.cpu.utils

import j1cpu.cpu.J1cpuConfig
import j1cpu.cpu.blackbox.{xpm_memory_dpdistram, xpm_memory_dpdistram_sim, xpm_memory_dpdistram_sim2}
import spinal.core._
import spinal.lib._

class Dram(depth: Int, width: Int, use4Data: Int, sim: Int) extends Component {
  val io = new Bundle {
    // a for write
    val ena = in Bool()
    val wea = in Bits ((if (use4Data == 0) 1 else (width / 8)) bits)
    val addra = in UInt (log2Up(depth) bits)
    val dina = in UInt (width bits)
    val douta = out UInt (width bits)

    // b for read
    val enb = in Bool()
    val addrb = in UInt (log2Up(depth) bits)
    val doutb = out UInt (width bits)
  }

  noIoPrefix()

  if (sim == 2) {
    val dram = new xpm_memory_dpdistram_sim2(depth, width)

    import io._

    dram.io.ena := ena
    dram.io.wea := wea
    dram.io.addra := addra
    dram.io.dina := dina
    douta := dram.io.douta

    dram.io.enb := enb
    dram.io.addrb := addrb
    doutb := dram.io.doutb
  }
  else if (sim == 0) {
    val dram = new xpm_memory_dpdistram(depth, width, use4Data)

    import io._

    dram.io.ena := ena
    dram.io.wea := wea
    dram.io.addra := addra
    dram.io.dina := dina
    douta := dram.io.douta

    dram.io.enb := enb
    dram.io.addrb := addrb
    doutb := dram.io.doutb
  }
  else {
    val dram = new xpm_memory_dpdistram_sim(depth, width, use4Data)

    import io._

    dram.io.ena := ena
    dram.io.wea := wea
    dram.io.addra := addra
    dram.io.dina := dina
    douta := dram.io.douta

    dram.io.enb := enb
    dram.io.addrb := addrb
    doutb := dram.io.doutb
  }
}

object dramGen {
  def main(args: Array[String]): Unit = {
    val spinalConfig = SpinalConfig(
      targetDirectory = "hw/gen",
      defaultConfigForClockDomains = J1cpuConfig().clockConfig
    )

    spinalConfig.generateVerilog(new Dram(256, 1, 0, 2))
  }
}
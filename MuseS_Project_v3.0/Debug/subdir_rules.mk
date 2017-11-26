################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Each subdirectory must supply rules for building sources it contributes
TEST_ONLY.cpp: ../TEST_ONLY.ino
	@echo 'Building file: $<'
	@echo 'Invoking: Resource Custom Build Step'
	
	@echo 'Finished building: $<'
	@echo ' '

%.o: ./%.cpp $(GEN_OPTS) | $(GEN_HDRS)
	@echo 'Building file: $<'
	@echo 'Invoking: GNU Compiler'
	"C:/ti/energia-1.6.10E18/hardware/tools/msp430/bin/msp430-gcc.exe" -c -mmcu=msp430f5529 -DF_CPU=16000000L -DENERGIA=17 -DARDUINO=101 -I"D:/Program Files/energia-0101E0017/hardware/msp430/variants/launchpad_f5529" -I"D:/Program Files/energia-0101E0017/hardware/msp430/cores/msp430" -I"D:/Project/TI Contest/Workspace_new/TEST_ONLY" -I"C:/ti/energia-1.6.10E18/hardware/tools/msp430/msp430/include" -Os -ffunction-sections -fdata-sections -g -gdwarf-3 -gstrict-dwarf -Wall -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o"$@" $(GEN_OPTS__FLAG) "$<"
	@echo 'Finished building: $<'
	@echo ' '

%.o: ../%.cpp $(GEN_OPTS) | $(GEN_HDRS)
	@echo 'Building file: $<'
	@echo 'Invoking: GNU Compiler'
	"C:/ti/energia-1.6.10E18/hardware/tools/msp430/bin/msp430-gcc.exe" -c -mmcu=msp430f5529 -DF_CPU=16000000L -DENERGIA=17 -DARDUINO=101 -I"D:/Program Files/energia-0101E0017/hardware/msp430/variants/launchpad_f5529" -I"D:/Program Files/energia-0101E0017/hardware/msp430/cores/msp430" -I"D:/Project/TI Contest/Workspace_new/TEST_ONLY" -I"C:/ti/energia-1.6.10E18/hardware/tools/msp430/msp430/include" -Os -ffunction-sections -fdata-sections -g -gdwarf-3 -gstrict-dwarf -Wall -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o"$@" $(GEN_OPTS__FLAG) "$<"
	@echo 'Finished building: $<'
	@echo ' '



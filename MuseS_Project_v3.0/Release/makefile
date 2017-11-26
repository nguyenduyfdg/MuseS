################################################################################
# Automatically-generated file. Do not edit!
################################################################################

SHELL = cmd.exe

CG_TOOL_ROOT := C:/ti/energia-1.6.10E18/hardware/tools/msp430

GEN_OPTS__FLAG := 
GEN_CMDS__FLAG := 

ORDERED_OBJS += \
"./Beacon.o" \
"./charset.o" \
"./DFPlayer_Mini_Mp3.o" \
"./PCD8544.o" \
"./TEST_ONLY.o" \
$(GEN_CMDS__FLAG) \
-Wl,--start-group \
-llpmsp430f5529_core \
-lm \
-Wl,--end-group \

-include ../makefile.init

RM := DEL /F
RMDIR := RMDIR /S/Q

# All of the sources participating in the build are defined here
-include sources.mk
-include subdir_vars.mk
-include subdir_rules.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(S_DEPS)),)
-include $(S_DEPS)
endif
ifneq ($(strip $(S_UPPER_DEPS)),)
-include $(S_UPPER_DEPS)
endif
ifneq ($(strip $(S62_DEPS)),)
-include $(S62_DEPS)
endif
ifneq ($(strip $(C64_DEPS)),)
-include $(C64_DEPS)
endif
ifneq ($(strip $(ASM_DEPS)),)
-include $(ASM_DEPS)
endif
ifneq ($(strip $(CC_DEPS)),)
-include $(CC_DEPS)
endif
ifneq ($(strip $(SV7A_DEPS)),)
-include $(SV7A_DEPS)
endif
ifneq ($(strip $(S55_DEPS)),)
-include $(S55_DEPS)
endif
ifneq ($(strip $(C67_DEPS)),)
-include $(C67_DEPS)
endif
ifneq ($(strip $(CLA_DEPS)),)
-include $(CLA_DEPS)
endif
ifneq ($(strip $(C??_DEPS)),)
-include $(C??_DEPS)
endif
ifneq ($(strip $(CPP_DEPS)),)
-include $(CPP_DEPS)
endif
ifneq ($(strip $(S??_DEPS)),)
-include $(S??_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(C62_DEPS)),)
-include $(C62_DEPS)
endif
ifneq ($(strip $(CXX_DEPS)),)
-include $(CXX_DEPS)
endif
ifneq ($(strip $(C++_DEPS)),)
-include $(C++_DEPS)
endif
ifneq ($(strip $(ASM_UPPER_DEPS)),)
-include $(ASM_UPPER_DEPS)
endif
ifneq ($(strip $(K_DEPS)),)
-include $(K_DEPS)
endif
ifneq ($(strip $(C43_DEPS)),)
-include $(C43_DEPS)
endif
ifneq ($(strip $(INO_DEPS)),)
-include $(INO_DEPS)
endif
ifneq ($(strip $(S67_DEPS)),)
-include $(S67_DEPS)
endif
ifneq ($(strip $(SA_DEPS)),)
-include $(SA_DEPS)
endif
ifneq ($(strip $(S43_DEPS)),)
-include $(S43_DEPS)
endif
ifneq ($(strip $(OPT_DEPS)),)
-include $(OPT_DEPS)
endif
ifneq ($(strip $(PDE_DEPS)),)
-include $(PDE_DEPS)
endif
ifneq ($(strip $(S64_DEPS)),)
-include $(S64_DEPS)
endif
ifneq ($(strip $(C_UPPER_DEPS)),)
-include $(C_UPPER_DEPS)
endif
ifneq ($(strip $(C55_DEPS)),)
-include $(C55_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 
EXE_OUTPUTS += \
MuseS_v3.0.out \

EXE_OUTPUTS__QUOTED += \
"MuseS_v3.0.out" \

BIN_OUTPUTS += \
MuseS_v3.0.hex \

BIN_OUTPUTS__QUOTED += \
"MuseS_v3.0.hex" \


# All Target
all: MuseS_v3.0.out secondary-outputs

dependents:
	-cd D:/Project/TI\ Contest/Workspace_new/lpmsp430f5529_core/Release && $(MAKE) clean all

# Tool invocations
MuseS_v3.0.out: $(OBJS) $(GEN_CMDS) D:/Project/TI\ Contest/Workspace_new/lpmsp430f5529_core/Release/liblpmsp430f5529_core.a
	@echo 'Building target: $@'
	@echo 'Invoking: GNU Linker'
	"C:/ti/energia-1.6.10E18/hardware/tools/msp430/bin/msp430-gcc.exe" -mmcu=msp430f5529 -DF_CPU=16000000L -DENERGIA=17 -DARDUINO=101 -O2 -Os -ffunction-sections -fdata-sections -Wall -Wl,-Map,"MuseS_v3.0.map" -Wl,--gc-sections -L"D:/Project/TI Contest/Workspace_new/lpmsp430f5529_core/Release" -Wl,--undefined,main -o"MuseS_v3.0.out" $(ORDERED_OBJS)
	@echo 'Finished building target: $@'
	@echo ' '

MuseS_v3.0.hex: $(EXE_OUTPUTS)
	@echo 'Invoking: GNU Objcopy Utility'
	"C:/ti/energia-1.6.10E18/hardware/tools/msp430/bin/msp430-objcopy.exe" -O ihex --remove-section .eeprom $(EXE_OUTPUTS__QUOTED) "MuseS_v3.0.hex"
	@echo 'Finished building: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(EXE_OUTPUTS__QUOTED)$(BIN_OUTPUTS__QUOTED)
	-$(RM) "Beacon.d" "DFPlayer_Mini_Mp3.d" "PCD8544.d" "TEST_ONLY.d" "charset.d" 
	-$(RM) "Beacon.o" "DFPlayer_Mini_Mp3.o" "PCD8544.o" "TEST_ONLY.o" "charset.o" 
	-@echo 'Finished clean'
	-@echo ' '

secondary-outputs: $(BIN_OUTPUTS)

.PHONY: all clean dependents
.SECONDARY:
D:/Project/TI\ Contest/Workspace_new/lpmsp430f5529_core/Release/liblpmsp430f5529_core.a:

-include ../makefile.targets


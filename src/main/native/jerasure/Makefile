SRCS             := *.cc
TARGET           := libjava-erasure.so
JERASURE_INCLUDE := /usr/local/include/jerasure

CC := g++
RM := rm

all: $(TARGET)

$(TARGET): $(SRCS)
	$(CC) -Wl,--add-stdcall-alias -I. -I$(JERASURE_INCLUDE) \
		-I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux \
		-lJerasure -lgf_complete -shared -o $@ $(SRCS)

clean:
	$(RM) -rf $(TARGET)

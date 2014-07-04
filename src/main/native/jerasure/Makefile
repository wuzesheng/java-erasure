SRCS             := *.cc
TARGET           := libjava-erasure.so
JERASURE_INCLUDE := /usr/local/include/jerasure

CC := g++
RM := rm

all: $(TARGET)

$(TARGET): $(SRCS)
	$(CC) -Wl,-soname,java-erasure.so -I. -I$(JERASURE_INCLUDE) \
		-I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux \
		-lJerasure -lgf_complete -shared -fPIC -o $@ $(SRCS)

clean:
	$(RM) -rf $(TARGET)

#include <mbed.h>
// #include "stm32f103c8t6.h"
#include <Nokia5110.h>
#include <msgpack-embedded.h>

union size_u { 
    uint8_t buf[4];
    uint32_t num;
};


uint8_t dog1[51] = {
    0x5E, 0x80, 0x00,
    0x7F, 0x80, 0x00,
    0x7F, 0xC0, 0x00,
    0xDB, 0xE0, 0x00,
    0xFF, 0xF0, 0x00,
    0xE7, 0xFC, 0x00,
    0xAD, 0xFF, 0xF8,
    0xC3, 0xFF, 0xC0,
    0xFF, 0xFF, 0xC0,
    0xFF, 0xFF, 0xC0,
    0xFF, 0xFF, 0xC0,
    0xFF, 0xFF, 0xC0,
    0xFF, 0xFF, 0x80,
    0x7F, 0xFF, 0x80,
    0x66, 0x19, 0x80,
    0x64, 0x11, 0x80,
    0x40, 0x01, 0x00
};
 
//    VCC,SCE,RST,D/C,MOSI,SCLK,LED
//N5110 lcd(p7,p8,p9,p10,p11,p13,p21);  // LPC1768 - pwr from GPIO
Nokia5110 display(PB_9, PB_8, PB_7, PB_5, PB_3);

//N5110 lcd(PTC9,PTC0,PTC7,PTD2,PTD1,PTC11);  // K64F - pwr from 3V3
 
Serial bt(PB_10, PB_11, 9600); // tx, rx 
Serial pc(PA_9, PA_10, 57300); // tx, rx

DigitalOut led(LED1);
#define BUF_SIZE 512

uint8_t buffers[BUF_SIZE][2];
size_u sizes[2];

uint8_t buffer_idx = 1;

uint8_t* buffer = (uint8_t*)&buffers;
size_u* size = (size_u*)&sizes;


bool n = false;
bool sr = false;
bool old = false;
unsigned int idx = 0;

uint8_t* get_buffer()
{
    return (uint8_t*)&buffers[buffer_idx];
}


size_u* get_size()
{
    return (size_u*)&sizes[buffer_idx];
}

void next_size() {
    buffer_idx = (buffer_idx + 1) % 2;
}

msgpack_zone mempool;

void print_str(const char* ptr, size_t size, uint8_t x, uint8_t y) {
    uint8_t x_current = x; 
    for(size_t idx = 0; idx < size; idx++){
        char current = *(ptr+idx);
        display.print_char(current, x_current, y);
        x_current += 6;
    }
}

void print_str(msgpack_object_str* str, uint8_t x, uint8_t y) {
    uint8_t x_current = x; 
    for(size_t idx = 0; idx < str->size; idx++){
        char current = *(str->ptr+idx);
        display.print_char(current, x_current, y);
        x_current += 6;
    }
}

int get_int(msgpack_object* obj, size_t idx) {
    if((obj+idx)->type == 2){
        return (obj+idx)->via.u64; 
    }
}

msgpack_object_str* get_str(msgpack_object* obj, size_t idx) {
    if((obj+idx)->type == 5){
        return &(obj+idx)->via.str; 
    }
}

msgpack_object deserialized;
int main()
{
    led = !led;

    msgpack_zone_init(&mempool, 2048);
    
    display.init(0x40);

    bt.baud(9600);

    bt.attach([] {
        auto size = get_size();
        auto buffer = get_buffer();

        if(n){
            buffer[idx++] = bt.getc();
            if(idx == size->num){
                pc.printf("%d bytes\n", idx);
                idx = 0;
                n = false;

                msgpack_unpack((const char*)buffer, size->num, NULL, &mempool, &deserialized);
                 /* print the deserialized object. */
                pc.printf("type: %d\n", deserialized.type);
                bt.printf("Received: %d bytes\n", size->num);
                
                
                old = false;
                // if(deserialized.type == 7){
                //     size_t map_size = deserialized.via.map.size;
                //     msgpack_object_kv* kv = deserialized.via.map.ptr;
                //     pc.printf("map size: %d\n", map_size);

                //     display.clear_buffer();

                //     for(size_t i = 0; i < map_size; i++){
                //         fwrite((kv+i)->key.via.str.ptr,(kv+i)->key.via.str.size, 1, pc);
                //         pc.putc(':');
                //         pc.putc(' ');
                //         fwrite((kv+i)->val.via.str.ptr,(kv+i)->val.via.str.size, 1, pc);
                //         print_str((kv+i)->val.via.str.ptr,(kv+i)->val.via.str.size, 0, i * 8);
                //         pc.putc('\n'); 
                //     }
                // }

                
            }
        }else{

            
            size->buf[idx++] = bt.getc();

            if(idx == 4){
                n = true;
                idx = 0;
                // size.num = swap_uint32(size.num);
                if(size->num >= 512){
                    n = false;
                    sr = true;
                }
                pc.printf("%d bytes\n", size->num);
            }
        }
    });

    pc.attach([] {
        bt.putc(pc.getc());
    });

    while(1) {
        pc.printf("working\n");
        wait(0.5);


        if(deserialized.type == 6 && old == false) {
            display.clear_buffer();

            msgpack_object_print(pc, deserialized);
            pc.putc('\n');

            size_t arr_size = deserialized.via.array.size;
            msgpack_object* array = deserialized.via.array.ptr;

            for(size_t idx = 0; idx < arr_size; idx += 3){
                msgpack_object_str* string = get_str(array, idx+2);
                uint32_t x =  get_int(array, idx);
                uint32_t y =  get_int(array, idx + 1);
                
                print_str(string, x, y);
                pc.printf("%d %d %d\n", 
                    x,
                    y,
                    string->size
                );
            }

            old = true;
        }

        if(sr == true){
            idx = 0;
            sr = false;
        }

        display.display();
        
        // bt.printf("h");
    }
}
 
## SYSViM
SYSViM，全称Stack-based YooScript Virtual Machine，用于运行翻译YooScript中间代码得到的“二进制”文件。

## 约定
全部大写的是指令或非通用寄存器

全部小写并且reg开头的是通用寄存器

value是常量，address是地址（对应data seg偏移量）

首字母大写的是字面量

其他寄存器一般不允许使用

ip是特例，代表下一个指令的位置

所有的指令长度均为3，不够的补全NOP

## 提醒
注意一些措辞上的区别，比如SMALL指令即使失败也**会**有CLRF的副作用。

## SYSViM指令集
### STOP NOP NOP
终止程序
### PUSH reg NOP
寄存器入栈
### POP reg NOP
栈顶元素出栈并且载入寄存器reg
### SAVE address reg
将reg中的值存入address中
### ADD （MUL） （DIV） （SUB） reg1 reg2
两寄存器之和入栈
### MOV reg1 reg2
将reg1赋值为reg2
### SET reg value
将reg赋值为value
### SETA address reg
将address赋值为reg中的值
### LODA reg1 address
将reg赋值为address中对应的值
### LOG reg NOP
打印reg
### IF ip NOP
若FLAG为1，跳转至ip
### IFN ip NOP
若FLAG为0，跳转至ip
### LODR reg NOP
栈顶元素不弹出地载入寄存器reg
### NOP 
什么也不做
### JUMP ip NOP
无条件跳转
### EQ reg1 reg2
若reg_a == reg_b, flag为1，否则为0
### NEQ reg1 reg2
若reg_a != reg_b, flag为1，否则为0
### SEQ reg1 reg2
若栈顶两元素相等, flag为1，否则为0
### SNEQ reg1 reg2
若栈顶两元素不等, flag为1，否则为0
### CLRF NOP NOP
设置FLAG为0
### BIG reg1 reg2
若 reg1 > reg2，则FLAG为1，否则为0
### LESS reg1 reg2
若 reg_1 < reg_2，则FLAG为1，否则为0
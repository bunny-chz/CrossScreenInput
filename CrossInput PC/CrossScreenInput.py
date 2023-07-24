import qrcode
from PIL import Image, ImageTk
import tkinter as tk
from flask import Flask, render_template, make_response, request
import logging
import pyautogui
import threading
import socket
import pyperclip
import requests


server = Flask(__name__)
log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

@server.route('/')
def index():
    # 渲染模板文件
    rendered_html = render_template('index.html')

    # 创建 HTTP 响应对象
    response = make_response(rendered_html)

    # 设置 MIME 类型为 text/html
    response.headers['Content-Type'] = 'text/html'

    return response

@server.errorhandler(404)
def page_not_found(e):
    return render_template('404.html'), 404

@server.route('/add', methods=['POST'])
def add():
    text_data = request.args.get('text')
    if not text_data:
        # 将字符串 "Hello, world!" 存储到剪贴板中
        pyperclip.copy(" ")
    else:
        # 将字符串 "Hello, world!" 存储到剪贴板中
        pyperclip.copy(text_data)
    # 模拟按下 Ctrl 键和 C 键
    pyautogui.hotkey('ctrl', 'v')
    # 渲染模板文件
    rendered_html = render_template('add.html')

    # 创建 HTTP 响应对象
    response = make_response(rendered_html)

    # 设置 MIME 类型为 text/html
    response.headers['Content-Type'] = 'text/html'

    return response

@server.route('/del')
def delete():
    pyautogui.keyDown('backspace')
    pyautogui.keyUp('backspace')
    
    # 渲染模板文件
    rendered_html = render_template('del.html')

    # 创建 HTTP 响应对象
    response = make_response(rendered_html)

    # 设置 MIME 类型为 text/html
    response.headers['Content-Type'] = 'text/html'

    return response
    
@server.route('/up')
def up():
    pyautogui.keyDown('up')
    pyautogui.keyUp('up')
    
    # 渲染模板文件
    rendered_html = render_template('up.html')

    # 创建 HTTP 响应对象
    response = make_response(rendered_html)

    # 设置 MIME 类型为 text/html
    response.headers['Content-Type'] = 'text/html'

    return response

@server.route('/down')
def down():
    pyautogui.keyDown('down')
    pyautogui.keyUp('down')
    
    # 渲染模板文件
    rendered_html = render_template('down.html')

    # 创建 HTTP 响应对象
    response = make_response(rendered_html)

    # 设置 MIME 类型为 text/html
    response.headers['Content-Type'] = 'text/html'

    return response

@server.route('/left')
def left():
    pyautogui.keyDown('left')
    pyautogui.keyUp('left')
    
    # 渲染模板文件
    rendered_html = render_template('left.html')

    # 创建 HTTP 响应对象
    response = make_response(rendered_html)

    # 设置 MIME 类型为 text/html
    response.headers['Content-Type'] = 'text/html'

    return response

@server.route('/right')
def right():
    pyautogui.keyDown('right')
    pyautogui.keyUp('right')
    
    # 渲染模板文件
    rendered_html = render_template('right.html')

    # 创建 HTTP 响应对象
    response = make_response(rendered_html)

    # 设置 MIME 类型为 text/html
    response.headers['Content-Type'] = 'text/html'

    return response

@server.route('/enter')
def enter():
    pyautogui.keyDown('enter')
    pyautogui.keyUp('enter')
    
    # 渲染模板文件
    rendered_html = render_template('enter.html')

    # 创建 HTTP 响应对象
    response = make_response(rendered_html)

    # 设置 MIME 类型为 text/html
    response.headers['Content-Type'] = 'text/html'

    return response

def start_server():
    server.run(host='0.0.0.0', port=52011)

def start_my_server(myport):
    server.run(host='0.0.0.0', port=myport)

class QrCodeGenerator(threading.Thread):
    def __init__(self, data, size=400, callback=None):
        super().__init__()
        self.data = data
        self.size = size
        self.callback = callback

    def run(self):
        qr = qrcode.QRCode(version=None, error_correction=qrcode.constants.ERROR_CORRECT_L, box_size=10, border=4)
        qr.add_data(self.data)
        qr.make(fit=True)
        img = qr.make_image(fill_color="black", back_color="white").convert('RGB')
        photo = ImageTk.PhotoImage(img.resize((self.size, self.size)))
        if self.callback:
            self.callback(photo)

class BunnyInput:
    def __init__(self, root):
        self.root = root
        
        # 创建一个标签，用于显示 IP 地址
        self.ip_label = tk.Label(root)
        self.ip_label.place(x=1, y=1)
        
        # 创建一个标签，用于显示二维码
        self.label = tk.Label(root)
        self.label.place(x=1, y=35)
        
        self.port_entry = tk.Entry(root)
        self.port_entry.place(x=10, y=458)
        
        # 创建启动服务器按钮
        self.start_button = tk.Button(root, text="自定义端口", command=self.start_my_server)
        self.start_button.place(x=230, y=452)
        
        # 创建一个标签，用于显示 自定义端口创建成功提示
        self.prompt_label = tk.Label(root)
        self.prompt_label.place(x=10, y=495)
        self.prompt_label.config(text= "端口范围是 0~65535")
        
        self.start_server()


    def update_image(self, photo):
        self.label.configure(image=photo)
        self.label.image = photo

    def generate_qr_code(self,port):
        # 获取本机 WLAN 的 IP 地址并更新标签
        ip_address = self.get_wlan_ip_address()
        self.ip_label.config(text= "本机IP：" + ip_address + "  默认端口：52011")
        generator = QrCodeGenerator("BunnyInput?http://" + ip_address + ":" + port, callback=self.update_image)
        generator.start()
       
        
    def get_wlan_ip_address(self):
        # 获取本机IP 地址
        hostname = socket.gethostname()
        ip_address = socket.gethostbyname(hostname)
        return ip_address
        
    def start_server(self):
        # 启动服务器
        t = threading.Thread(target=start_server)
        t.start()
    def start_my_server(self):
        port = self.port_entry.get()
        # 启动自定义服务器
        t = threading.Thread(target=start_my_server, args=(port,))
        t.start()
        self.generate_qr_code(port)
        self.prompt_label.config(text= "自定义的端口 " + port + " 已启用，请扫码连接")

if __name__ == '__main__':
    root = tk.Tk()
    app = BunnyInput(root)
    app.generate_qr_code("52011")
    root.geometry('400x520+0+0')
    root.title('跨屏输入')
    root.iconbitmap('app_icon.ico')
    root.mainloop()
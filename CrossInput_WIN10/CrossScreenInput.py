import qrcode
from PIL import ImageTk
import tkinter as tk
from tkinter import messagebox
from flask import Flask, render_template, make_response, request, send_file
import logging
import pyautogui
import threading
import socket
import pyperclip
import os


WINDOW_WIDTH = 400
WINDOW_HEIGHT = 575

server = Flask(__name__)
log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

address = '0.0.0.0:52011'

def render_template_with_mapper(template_name):
    rendered_html = render_template(template_name)

    response = make_response(rendered_html)

    response.headers['Content-Type'] = 'text/html'

    return response

@server.route('/')
def index():
    return render_template_with_mapper('index.html')

@server.route('/webui')
@server.route('/webui.html')
def webui():
    rendered_html = render_template('webui.html', SERVER_ADDR=address)

    response = make_response(rendered_html)

    response.headers['Content-Type'] = 'text/html'

    return response

@server.route('/about')
@server.route('/about.html')
def about():
    return render_template_with_mapper('about.html')

@server.route('/<path:filename>')
def custom_static(filename):
    if os.path.exists(filename):
        return send_file(filename, as_attachment=True)
    else:
        return render_template('404.html'), 404

@server.route('/test')
def test():
    return render_template_with_mapper('test.html')

@server.errorhandler(404)
def page_not_found(e):
    return render_template('404.html'), 404

@server.route('/add', methods=['POST'])
def add():
    text_data = request.args.get('text')
    if not text_data:
        # 将空格字符串 "" 存储到剪贴板中
        pyperclip.copy(" ")
    else:
        # 将字符串 "Hello, world!" 存储到剪贴板中
        pyperclip.copy(text_data)
    pyautogui.hotkey('ctrl', 'v')
    return render_template_with_mapper('add.html')

@server.route('/del')
def delete():
    pyautogui.keyDown('backspace')
    pyautogui.keyUp('backspace')
    return render_template_with_mapper('del.html')
    
@server.route('/up')
def up():
    pyautogui.keyDown('up')
    pyautogui.keyUp('up')
    return render_template_with_mapper('up.html')

@server.route('/down')
def down():
    pyautogui.keyDown('down')
    pyautogui.keyUp('down')
    return render_template_with_mapper('down.html')

@server.route('/left')
def left():
    pyautogui.keyDown('left')
    pyautogui.keyUp('left')
    return render_template_with_mapper('left.html')

@server.route('/right')
def right():
    pyautogui.keyDown('right')
    pyautogui.keyUp('right')
    return render_template_with_mapper('right.html')

@server.route('/enter')
def enter():
    pyautogui.keyDown('enter')
    pyautogui.keyUp('enter')
    return render_template_with_mapper('enter.html')

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

class BunnyLogging:
    def __init__(self, name = 'bunny_input_log', level=logging.DEBUG):
        self.logger = logging.getLogger(name)
        self.logger.setLevel(level)

        file_handler = logging.FileHandler('BunnyInputLog.log')
        file_handler.setLevel(level)

        formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
        file_handler.setFormatter(formatter)

        self.logger.addHandler(file_handler)

    def debug(self, message):
        self.logger.debug(message)

    def info(self, message):
        self.logger.info(message)

    def warning(self, message):
        self.logger.warning(message)

    def error(self, message):
        self.logger.error(message)

    def critical(self, message):
        self.logger.critical(message)

class BunnyInput:
    def __init__(self, root, bunny_log : BunnyLogging):
        self.root = root
        self.bunny_log = bunny_log

        self.ip_label = tk.Label(root)
        self.ip_label.place(x=1, y=1)

        self.label = tk.Label(root)
        self.label.place(x=1, y=30)

        self.prompt_label = tk.Label(root)
        self.prompt_label.place(x=1, y=435)
        self.prompt_label.config(text= "支持所有智能联网设备跨屏输入(在相同Wi-Fi局域网下)", font=("", 9,"bold"), fg="red")

        self.start_button = tk.Button(root, text="复制网页界面地址", command=self.copy_server_addr)
        self.start_button.place(x=15, y=467)

        self.prompt_label = tk.Label(root)
        self.prompt_label.place(x=160, y=470)
        self.prompt_label.config(text= "建议用微信扫一扫连接", font=("", 12,"bold"), fg="green")
        
        self.port_entry = tk.Entry(root)
        self.port_entry.insert(0, "请点击此处，输入端口号")
        self.port_entry.bind("<FocusIn>", self.on_entry_focus_in)
        self.port_entry.bind("<FocusOut>", self.on_entry_focus_out)
        self.port_entry.place(x=25, y=520)

        self.start_button = tk.Button(root, text="自定义端口", command=self.start_my_server)
        self.start_button.place(x=255, y=513)

        self.prompt_label = tk.Label(root)
        self.prompt_label.place(x=25, y=545)
        self.prompt_label.config(text= "端口范围是 0~65535")

        self.start_server()

    def update_image(self, photo):
        self.label.configure(image=photo)
        self.label.image = photo

    def generate_qr_code(self,port):
        ip_address = self.get_wlan_ip_address()
        self.ip_label.config(text= "本机IP：" + ip_address + "  默认端口：52011")
        generator = QrCodeGenerator(f"http://{ip_address}:{port}/webui", callback=self.update_image)
        generator.start()
        global address
        address = 'http://' + ip_address + ':' + port + '/'
        self.bunny_log.debug(f"Generate QR Code, Address : {address}")
        
    def get_wlan_ip_address(self):
        hostname = socket.gethostname()
        ip_address = socket.gethostbyname(hostname)
        self.bunny_log.debug(f"Get wlan ip address : {ip_address}")
        return ip_address
        
    def start_server(self):
        t = threading.Thread(target=start_server)
        t.start()
        self.bunny_log.debug(f"Start default Flask server now")
    
    def start_my_server(self):
        port = self.port_entry.get()
        if self.check_port() :
            t = threading.Thread(target=start_my_server, args=(port,))
            t.start()
            self.generate_qr_code(port)
            self.prompt_label.config(text= "自定义的端口 " + port + " 已启用，请重新扫码连接")
            self.bunny_log.debug(f"Start Flask server by my port {port}")

    def copy_server_addr(self):
        pyperclip.copy(address + 'webui')

    def on_entry_focus_in(self, event):
        if self.port_entry.get() == "请点击此处，输入端口号":
            self.port_entry.delete(0, tk.END)
    
    def on_entry_focus_out(self, event):
        if self.port_entry.get() == "":
            self.port_entry.insert(0, "请点击此处，输入端口号")

    def check_port(self):
        result = True
        port_text = self.port_entry.get()
        if len(port_text) != 0:
            try:
                port = int(port_text)
                if not 0 <= port <= 65535:
                    result = False
                    messagebox.showwarning("无效的端口号", "端口范围是 0~65535")
                    self.bunny_log.warning("Invalid port number, out of Out of range, 0~65535")
            except ValueError:
                messagebox.showwarning("无效的端口号", "端口是整数，且范围是 0~65535")
                self.bunny_log.warning("Invalid port number, not an integer")
                result = False
        else:
            messagebox.showwarning("无效的端口号", "端口不能为空！端口是整数，且范围是 0~65535")
            self.bunny_log.warning("Invalid port number, length is <= 0")
            result = False
        return result

if __name__ == '__main__':
    bunny_log = BunnyLogging()
    bunny_log.info('============= Starting the application =============')
    root = tk.Tk()
    app = BunnyInput(root, bunny_log)
    screen_width = root.winfo_screenwidth()
    screen_height = root.winfo_screenheight()
    app.generate_qr_code("52011")
    bunny_log.info('Create QR 52011 Code')
    root.geometry(f'{WINDOW_WIDTH}x{WINDOW_HEIGHT}+{(screen_width - WINDOW_WIDTH) // 2}+{(screen_height - WINDOW_HEIGHT) // 2}')
    root.title('跨屏输入')
    root.iconbitmap('app_icon.ico')
    bunny_log.info('============= Inited APP =============')
    root.mainloop()
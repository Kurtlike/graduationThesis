import os
import socket
import tensorflow as tf
from tensorflow.keras import datasets, layers, models, losses
def find_num(arr):
    max = 0
    n = 0
    for i in range(0, arr.size):
        if arr[0][i] > max:
            max = arr[0][i]
            n = i
    return n

def client_program():
    reconstructed_model = tf.keras.models.load_model('neiro/alex_num_3')
    host = "localhost"
    port = 6666

    client_socket = socket.socket()
    client_socket.connect((host, port))

    while(True):
        img_name = client_socket.recv(1024).decode()
        img_name = img_name[:-1]
        img_path = "/home/kurtlike/neiro/PCtest/data/" + img_name
        img = tf.io.read_file(img_path)
        tensor = tf.io.decode_image(img, channels=3, dtype=tf.dtypes.float32)
        tensor = tf.image.resize(tensor, [32, 32])
        input_tensor = tf.expand_dims(tensor, axis=0)
        h = reconstructed_model.predict(input_tensor)
        num = find_num(h)
        mess = str(num) + "\n"
        client_socket.send(mess.encode())
        os.remove(img_path)
    client_socket.close()


if __name__ == '__main__':
    client_program()


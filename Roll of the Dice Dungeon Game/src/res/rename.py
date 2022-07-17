# rename all files in cards/ to card_<number>

import os
import re
import shutil

index = 0
for filename in os.listdir('./cards/'):
    if filename.endswith('.png'):
        print(filename)
        new_name = 'card_' + str(index + 1) + '.png'
        print(new_name)
        shutil.move('./cards/' + filename, './cards/' + new_name)
        print('renamed')
    else:
        print('not a png')
        print(filename)
        print('not renamed')
        print('\n')
    index += 1

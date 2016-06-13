#!/usr/bin/env python
# -*- coding: utf-8 -*-
import numpy as np

def new_board():
    init = np.array([
        [0,0,0,0,0,0,0,0],
        [0,0,0,0,0,0,0,0],
        [0,0,0,0,0,0,0,0],
        [0,0,0,1,2,0,0,0],
        [0,0,0,2,1,0,0,0],
        [0,0,0,0,0,0,0,0],
        [0,0,0,0,0,0,0,0],
        [0,0,0,0,0,0,0,0],
        ])
    return init.copy()

def slice_brd(brd, sx, ex, px, sy, ey, py):
    def slice_ex(start,end,step):
        if step == 0:
            return start
        else:
            return slice(start,end,step)
    tmp = brd[slice_ex(sy,ey,py),slice_ex(sx,ex,px)]

    if tmp.ndim == 2:
        tmp = np.diagonal(tmp)
        tmp.setflags(write=True)
    return tmp

def update_impl(brd, typ, x, y, sx, sy):
    tmp = slice_brd(brd, x+sx, None, sx, y+sy, None, sy)
    #print tmp
    c = 0
    ntyp = 2 if typ == 1 else 1
    for i in np.nditer(tmp, order='F'):
        if i == ntyp:
            c += 1
            continue
        elif i == typ and c != 0:
            tmp = slice_brd(brd,
                    x+sx, x+((c+1)*sx), sx,
                    y+sy, y+((c+1)*sy), sy)
            tmp[...] = typ
        break

step_cache = [
    [-1,0],
    [-1,-1],
    [0,-1],
    [1,-1],
    [1,0],
    [1,1],
    [0,1],
    [-1,1],
    ]

def update(brd, typ, x, y):
    brd[y,x] = typ
    for i in step_cache:
        update_impl(brd, typ, x, y, i[0], i[1])

# debug func
def force_set(brd, typ, x, y):
    brd[y, x] = typ

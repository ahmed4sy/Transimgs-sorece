import easyocr
import json
import numpy as np
rdr = easyocr.Reader(['en'],gpu=False)
def main():
    with open("Jython/marge.json","r") as js:
        jse = json.loads(js.read())
    ocr = rdr.readtext(jse['sorece'])
    res = np.array(bubbles(ocr), dtype=np.int64)
    res = res.tolist()
    with open("Jython/marge.json","w") as jses:
        jse["State"] = True
        jse["dataOCR"] = res
        json.dump(jse,jses)


def bubbles(ocr:list):
    descent = True
    root = [ocr[0][0]]
    tmp = ocr[0][0][0]
    list = []
    limitx = 90
    limity = 90
    n = 0
    Boolen = [True]
    on = False
    ls = 1
    for i in range(1,len(ocr)):
       Boolen.append(False)
    while descent:
        s = 0
        n += 1
        for i in range(ls,len(ocr)):
            if tmp[1] <= ocr[i][0][0][1] and  ocr[i][0][0][1] <= tmp[1]+limity and ocr[i][0][0][0]  <= tmp[0]+limitx and ocr[i][0][0][0]  >= tmp[0]-limitx:
                on = True
                Boolen[i] = True
                root.append(ocr[i][0])
                tmp = ocr[i][0][0]
        list.append(root)
        #error func
        if len(ocr) != len(Boolen):
            print("ERORR: BOOL")
            break
        if n == 100:
            print("ERORR: LOOP")
            print('bool:',Boolen)
            print('tmp:',tmp)
            break
        #try exit for loop
        for i in range(len(Boolen)):
            if Boolen[i] == False:
                tmp = ocr[i][0][0]
                Boolen[i] = True
                root = []
                ls = i
                break
            else:
                s+=1
                if s == len(Boolen):
                    descent = False
    # read array
    result = []
    tmp = list[0][0]
    s = 0
    # end = len(list)
    # while s == end:
    #     for l in range(s,len(list)):
    #         if tmp[0][1] < list[l][0][0][1] and list[l][0][0][1] < tmp[0][1]+limit:
    #             tmp += list[l]
    #             list.remove(list[l])
    #     s+=1
    #     tmp = list[s][0]

    # for r in range(len(list)):
    #     xtmp=list[r][0][0][0]
    #     ytmp=list[r][0][0][1]
    #     for lo in range(len(list[r])):
    #         if xtmp > list[r][lo][0][0]:
    #             xtmp =list[r][lo][0][0]
    #     X = xtmp
    #     Y = ytmp
    #     H = list[r][len(list[r])-1][2][1]
    #     W = list[r][len(list[r])-1][2][0]

    #     index =(X-60,Y-10,W+55,H+10)
    #     result.append(index)

    for loopbub in range(len(list)):
        Xtmp,Ytmp,Wtmp,Htmp = list[loopbub][0][0][0],list[loopbub][0][0][1],list[loopbub][0][2][0],list[loopbub][0][2][1]
        for posBub in range(1,len(list[loopbub])):
            if Xtmp > list[loopbub][posBub][0][0]:
                Xtmp = list[loopbub][posBub][0][0]
            if Wtmp < list[loopbub][posBub][2][0]:
                Wtmp = list[loopbub][posBub][2][0]
            Htmp = list[loopbub][posBub][2][1]
        index =[Xtmp-5,Ytmp-5,Wtmp+5,Htmp+5]
        result.append(index)

    return result

main()
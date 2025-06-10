from random import randrange


def tomato():
    seisu = []
    for _ in range(100):
        seisu.append(randrange(100))
    seisu2 = []
    for x in range(100):
        if x not in seisu:
            seisu2.append(x)
    return seisu2


tomato()
###########################################################################
# FILE: ex11_utils.py
# EXERCISE: Intro2cs ex11 2022-2023
# WRITER: Intro2cs staff: Jana Hirbawi & Nicole Aguilar
# DESCRIPTION: This file is responsible for the functions given in ex_11
# to code.
###########################################################################
from typing import List, Tuple, Iterable, Optional

Board = List[List[str]]
Path = List[Tuple[int, int]]


###########################################################################
#                       CODING OF THE FUNCTIONS                           #
###########################################################################


def set_words(words: Iterable[str]) -> Iterable[str]:
    """
    This function takes words as an argument and is used to return a set
    of the words contents.
    :param words: an iterable data type of the words given in the
    boggle_dict.txt file.
    :return: a set of all the words in words if words isn't  a set,
    otherwise return words.
    """
    if type(words) != set:
        lst = []
        for word in words:
            lst.append(word)
        return set(lst)
    return words


def get_set_word(words: Iterable[str]) -> Optional[set[str]]:
    """
    This function takes words as an argument and is responsible for
    collecting the valid order of the letters of each word.
    :param words: an iterable data type of the words given in the
    boggle_dict.txt file.
    :return: a set data type.
    """
    if not words:
        return set()

    word_lst = []
    for word in words:
        add_word = ""
        for letter in range(len(word)):
            add_word += word[letter]
            word_lst.append(add_word)
    return set(word_lst)


def get_check_lst(coord: tuple, len_board: tuple, lst: list):
    """
    This function is used to get all the neighbor tuples of the tuple
    coord in the board, and adds them to the parameter lst.
    :param coord: a tuple of the formate tuple(row, col) data type.
    :param len_board: a tuple of the formate tuple(int, int) data type.
    :param lst: an empty lst data type.
    :return: lst with the added tuples.
    """
    rows, cols = len_board
    for row in [-1, 0, 1]:
        for col in [-1, 0, 1]:
            new_row, new_col = coord[0] + row, coord[1] + col
            if 0 <= new_row <= rows and 0 <= new_col <= cols:
                lst.append((new_row, new_col))
    return lst


def check_path(coord: tuple, lst: list, board: Board):
    """
    This function is a helper function for the function is_valid_path()
    and is used to track whether the path is valid or not.
    :param coord: a tuple of the formate tuple(row, col) data type.
    :param lst: a list data type.
    :param board: a list of lists of strings of size 4 * 4 data type.
    :return: (True , str) if the coordinates valid, (False, 0) otherwise.
    """
    rows, cols = len(board) - 1, len(board[0]) - 1

    if coord in lst:  # check if coordinate in the list of checked tuples
        return False, ""

    if coord[0] > rows or coord[1] > cols:
        # check coordinate is with the boundaries of the board
        return False, ""

    if len(lst) == 0:
        return True, board[coord[0]][coord[1]]

    else:
        if coord in get_check_lst(lst[len(lst) - 1], (rows, cols), []):
            return True, board[coord[0]][coord[1]]
        return False, ""


def is_valid_path(board: Board, path: Path, words: Iterable[str]) -> Optional[
                                                                    str]:
    """
    This function takes three parameters board, path and words and is
    used to check whether the path given is legal or not. To add,
    it needs to check if the word that we got from the path is in fact
    in the parameter words we got.
    :param board: a list of lists of strings of size 4 * 4 data type.
    :param path: a list of tuple of the formate list[tuple(row,
    col)] data type.
    :param words: an iterable data type of the words given in the
    boggle_dict.txt file.
    :return: the word we got from the path, if the path and word legal,
    otherwise return None.
    """
    words = get_set_word(words)
    if len(path) == 0 or len(words) == 0:
        return None

    lst = []
    word = ""
    for word_path in path:
        check_word_path = check_path(word_path, lst, board)
        if not check_word_path[0]:
            return None

        lst.append(word_path)
        word += check_word_path[1]
        if word not in words:
            return None

    return word


def find_n_paths_words(n: int, coor: tuple, board: Board, boun: tuple,
                       words: Iterable[str],
                       check_words: set[str], word: str, path_lst: list,
                       final_lst: list, flag: int):
    """
    This function is a helper function for find_length_n_paths() as it
    finds the path of the length n on the board that constructs a word
    in words by using backtracking and updates the list final_lst
    accordingly.
    :param n: an integer number data type.
    :param coor: a tuple of the formate tuple[int, int] data type.
    :param board: a list of lists of strings of size 4 * 4 data type.
    :param boun: a tuple of the formate tuple[int, int] data type.
    :param words: an iterable data type of the words given in the
    boggle_dict.txt file.
    :param check_words: a set of the formate set[str] data type.
    :param word: a string data type.
    :param path_lst: a list of the formate list[tuple[int, int]] data type.
    :param final_lst: a list of the formate list[list[tuple[int, int]]] data type.
    :param flag: an integer number data type.
    :return: None
    """
    if word not in check_words:
        return

    if n == 0:
        if path_lst not in final_lst:
            if word in words:
                final_lst.append(path_lst[:])
        return

    for i in [-1, 0, 1]:
        for j in [-1, 0, 1]:
            new_coor = (coor[0] + i, coor[1] + j)

            if 0 <= new_coor[0] <= boun[0] and 0 <= new_coor[1] <= boun[1] \
                    and new_coor not in path_lst:
                path_lst.append(new_coor)
                letter_to_add = board[new_coor[0]][new_coor[1]]
                len_str = len(letter_to_add)
                word += letter_to_add

                if flag == 0:
                    red = 1
                else:
                    red = len_str

                find_n_paths_words(n - red, new_coor, board, boun, words,
                                   check_words,
                                   word, path_lst, final_lst, flag)
                path_lst.pop()
                word = word[:-len_str]
    return


def get_words_n(n: int, words: Iterable[str]):
    """
    This function takes two parameters n and words, and is used to
    return all the words that their lengths are equal or less than n.
    :param n: an integer number data type.
    :param words: an iterable data type of the words given in the
    boggle_dict.txt file.
    :return: a set of all the words that met the requirement of the length.
    """
    words = [word for word in words if len(word) >= n]
    return set(words)


def find_length_n_paths(n: int, board: Board, words: Iterable[str]) -> List[
                                                                        Path]:
    """
    This function takes three parameters n, board and words and is used
    to find all the legal paths of  the length n that gives legal words
    that are found in the words set.
    :param n: an integer number that expresses the length of the path.
    :param board: a list of lists of strings of size 4 * 4 data type.
    :param words: an iterable data type of the words given in the
    boggle_dict.txt file.
    :return: a list of the format [list [tuple(row, col)]] as where
    the path is added to returned list as list [tuple(row, col)]. If
    there are more than one path to the same word we should add them all.
    """
    if n == 0:
        return []

    words, words_check = get_words_n(n, words), get_set_word(words)
    row, col = len(board) - 1, len(board[0]) - 1
    board_coordinates = [(r, c) for r in range(row + 1) for c in
                         range(col + 1)]
    final_lst = []

    for coor in board_coordinates:
        find_n_paths_words(n - 1, coor, board, (row, col), words, words_check,
                           board[coor[0]][coor[1]], [coor], final_lst, 0)
    return final_lst


def find_length_n_words(n: int, board: Board, words: Iterable[str]) -> List[
                                                                        Path]:
    """
    This function takes three parameters n, board and words and is used
    to find all the legal paths of the length n of the words in the
    words set.
    :param n: an integer number that expresses the length of the word
    that we should look for.
    :param board: a list of lists of strings of size 4 * 4 data type.
    :param words: an iterable data type of the words given in the
    boggle_dict.txt file.
    :return: a list of the format [list [tuple(row, col)]] as where
    the path is added to returned list as list [tuple(row, col)]. If
    there are more than one path to the same word we should add them all.
    """
    if n == 0:
        return []

    words, words_check = get_words_n(n, words), get_set_word(words)
    row, col = len(board) - 1, len(board[0]) - 1

    board_coordinates = [(r, c) for r in range(row + 1) for c in
                         range(col + 1)]
    final_lst = []
    for coor in board_coordinates:
        len_str = len(board[coor[0]][coor[1]])
        find_n_paths_words(n - len_str, coor, board, (row, col), words,
                           words_check,
                           board[coor[0]][coor[1]], [coor], final_lst, 1)
    return final_lst


def compare_path(path_1, path_2):
    """
    This function is used to check which path is of the two given -
    path_1 and path_2 - is longer.
    :param path_1: a list of the formate list[tuple[int, int]] data type.
    :param path_2: a list of the formate list[tuple[int, int]] data type.
    :return: path_1 if len(path_1) > len(path_2), otherwise path_2.
    """
    if len(path_1) > len(path_2):
        return path_1
    return path_2


def max_score_helper(n, coor: tuple, board: Board, boun: tuple,
                     words: Iterable[str],
                     check_words: set[str], word: str, path_lst: list,
                     word_dict: dict):
    """
    This function is a helper function for the function max_score_paths().
    :param n: an integer number data type.
    :param coor: a tuple of the formate tuple[int, int] data type.
    :param board: a list of lists of strings of size 4 * 4 data type.
    :param boun: a tuple of the formate tuple[int, int] data type.
    :param words: n iterable data type of the words given in the
    boggle_dict.txt file.
    :param check_words: a set of the formate set[str] data type.
    :param word: a string data type.
    :param path_lst: a list of the formate list[tuple[int, int]] data type.
    :param word_dict: a dict data type
    :return: None
    """
    if len(word) > n or word not in check_words:
        return

    elif word in words:
        if word in word_dict:
            new_value = compare_path(path_lst[:], word_dict[word])
            word_dict[word] = new_value
        else:
            word_dict[word] = path_lst[:]

    for i in [-1, 0, 1]:
        for j in [-1, 0, 1]:
            new_coor = (coor[0] + i, coor[1] + j)

            if 0 <= new_coor[0] <= boun[0] and 0 <= new_coor[1] <= boun[1] \
                    and new_coor not in path_lst:
                path_lst.append(new_coor)
                letter_to_add = board[new_coor[0]][new_coor[1]]
                len_str = len(letter_to_add)
                word += letter_to_add
                max_score_helper(n, new_coor, board, boun, words,
                                 check_words, word, path_lst, word_dict)
                path_lst.pop()
                word = word[:-len_str]

    return


def max_score_paths(board: Board, words: Iterable[str]) -> List[Path]:
    """
    This function takes two parameters board and words,
    :param board: a list of lists of strings of size 4 * 4 data type.
    :param words: an iterable data type of the words given in the
    boggle_dict.txt file.
    :return: a list of the paths of the legal words that gives the
    highest score. We should return the path that gives the highest
    score if there is more than one path for the same word.
    """
    word_dict = dict()
    words_check = get_set_word(words)
    words = set_words(words)
    n = len(max(words, key=len))
    row, col = len(board) - 1, len(board[0]) - 1

    board_coordinates = [(r, c) for r in range(row + 1) for c in
                         range(col + 1)]

    for coor in board_coordinates:
        max_score_helper(n, coor, board, (row, col), words, words_check,
                         board[coor[0]][coor[1]], [coor], word_dict)

    return [value for value in word_dict.values()]

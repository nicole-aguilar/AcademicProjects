###########################################################################
# FILE: functional_part.py
# EXERCISE: Intro2cs ex11 2022-2023
# WRITER: Jana Hirbawi & Nicole Aguilar
# DESCRIPTION: This file is responsible for the functions and the rules of
# the Boogle Game
###########################################################################

######################## IMPORTS ####################################

from GUI import *
from ex11_utils import is_valid_path
from typing import List, Tuple

######################################################################
Board = List[List[str]]
Path = List[Tuple[int, int]]


class ButtonsCommand:
    def __init__(self, window: GUIGame, words):
        self.gui = window
        self.words = words
        self.window = window.get_window()
        self.initial_frame = window.get_initial_frame()
        self.game_frame = window.get_game_frame()
        self.final_frame = window.get_final_frame()
        self.board_frame = window.get_board_frame()

    def exit_game(self):
        self.window.destroy()

    def start_game(self) -> None:
        """Defines what to do the moment the player chooses to start the
        game. Forgets the initial frame, shows the game frame, starts
        running the timer."""
        # Forget the Initial Frame
        self.initial_frame.pack_forget()
        # Show the Game Frame
        self.game_frame.pack(side=tk.TOP, fill=tk.BOTH, expand=True)
        # Start the music
        self.gui.play_music()
        # Start the timer
        self.gui.get_countdown(0, 5, self.game_frame)

    def play_again(self) -> None:
        """Command for the play again button, resets all the parameters of
        the game frame"""
        self.final_frame.pack_forget()
        # Reset the all the widgets of the game_frame
        self.gui.get_listbox().delete(0, tk.END)
        self.gui.play_music()
        self.gui.get_countdown(3, 0, self.game_frame)
        self.gui.set_word_label_empty()
        # self.gui.set_score_label_zero()
        self.gui.set_found_path_words_empty()
        self.gui.set_found_word_empty()
        self.game_frame.pack(side=tk.TOP, fill=tk.BOTH,
                             expand=True)
        self.gui.get_generate_boggle_board(self.board_frame,
                                           randomize_board())

    def check_word(self):
        """
        This method is used to construct the command for the button
        check in the Frame game_frame.
        :return:
        """
        words = self.words
        path_lst = self.gui.get_collected_path()
        board = self.gui.get_board_game()
        word = is_valid_path(board, path_lst, words)
        found_words = self.gui.get_found_word()

        if word and word not in found_words:
            # this function will return word if the path and word are valid,
            # otherwise None
            self.gui.get_listbox().insert("end", word)
            found_words.append(word)
            self.gui.set_updated_score()
            self.gui.set_word_label_empty()

        else:  # the path is incorrect or the word is not in words
            self.gui.set_word_label_empty()

        self.gui.set_collected_path_empty()
        # Returns the buttons that where clicked to their default color
        self.gui.set_button_color_default()

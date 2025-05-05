# ##########################################################################
# FILE: ex11_utils.py EXERCISE: Intro2cs ex11 2022-2023 WRITER: Jana Hirbawi
# & Nicole Aguilar DESCRIPTION: This file is responsible for the graphic of
# the Game HELP:https://www.geeksforgeeks.org/create-countdown-timer-using
# -python -tkinter/, https://stackoverflow.com/questions/2556108/rreplace
# -how-to-replace-the-last-occurrence-of-an-expression-in-a-string
# ##########################################################################

################ IMPORTS ֳֳֳֳֳֳֳֳֳֳֳֳֳֳֳֳֳ##################################
import tkinter as tk
from PIL import ImageTk, Image
from boggle_board_randomizer import *
from pygame import mixer


############################################################

################### CONSTANTS #########################
LETTER_TYPE = "ravie"
LETTERS_COLOR = "navy"
BUTTON_REG_COLOR = "white"
BUTTON_ACTIVE_COLOR = "DeepPink2"
IMAGE_BG = "background_for_game.jpg"
GAME_MUSIC = "alex-productions-good-vibes.mp3"
BOARD_SIZE = 4
INSTRUCTIONS = "Find all the words you can in\nunder 3 minutes.\n" \
               "Use only neighboring letters."


##########################################################


def rreplace(s, old, new, occurrence):
    """This functions replaces the element starting from the right"""
    li = s.rsplit(old, occurrence)
    return new.join(li)


class GUIGame:
    
    """Class that is in charge of the graphic design that the user can see
    when running. It doesnt knows the rules of the game"""

    def __init__(self):
        """Initializes the GUIGame class, consisting of 3 frames, a welcome,
        game frame and ending frame. """

        # self.music = "bgmusic.wav"
        self.lst_buttons = []  # lst of buttons of the type tk buttons
        self.__collected_path = []  # lst of the path of the word
        self.__found_words = []  # lst of strings of the words the player found
        self.__board_game = []
        self.__score = 0
        self.music = GAME_MUSIC
        ###################################################################

        self.__root = tk.Tk()  # root to create a window
        self.__root.title("Let's play Boggle!")  # the title of the window
        self.__root.geometry("600x400")  # the size of the window
        self.__root.resizable(False, False)  # you can't change the size
        self.__root.config(cursor="spraycan")  # to change the mouse.

        ###################################################################
        # First Layer: Initial Frame
        ###################################################################
        self.__initial_frame = tk.Frame(self.__root)
        self.__initial_frame.pack(expand=True, fill=tk.BOTH, side=tk.TOP)

        # Load the background image
        self.__image = Image.open(IMAGE_BG)
        self.__image = self.__image.resize((600, 400), Image.LANCZOS)
        self.__background_image = ImageTk.PhotoImage(self.__image)

        # Creates the canvas where the image, text, and the button are placed
        self.__first_canvas = tk.Canvas(self.__initial_frame)

        self.__first_canvas.create_image(0, 0, image=self.__background_image,
                                         anchor="nw")
        self.__first_canvas.create_text(200, 100,
                                        text="Welcome to \n    Boggle!",
                                        fill=LETTERS_COLOR,
                                        font=(LETTER_TYPE, 32))
        self.__first_canvas.create_text(210, 220,
                                        text=INSTRUCTIONS,
                                        fill=LETTERS_COLOR,
                                        font=(LETTER_TYPE, 15))

        ###################################################################
        # Create buttons part for the initial frame:
        ###################################################################
        self.__exit_button_f1 = tk.Button(self.__first_canvas, text="Exit",
                                          font=(LETTER_TYPE, 9),
                                          fg=LETTERS_COLOR,
                                          bg=BUTTON_REG_COLOR)
        self.__exit_button_f1.pack(side=tk.BOTTOM, pady=2,
                                   padx=1)

        self.__start_button = tk.Button(self.__first_canvas, text="Start Game",
                                        font=(LETTER_TYPE, 20),

                                        fg=LETTERS_COLOR, bg=BUTTON_REG_COLOR,
                                        padx=1,
                                        pady=1)

        self.__start_button.pack(side="bottom",
                                 pady=10)

        self.__first_canvas.pack(fill=tk.BOTH, expand=True)

        ###################################################################
        # Second Layer: Game Frame
        ###################################################################

        self.__game_frame = tk.Frame(self.__root, bg="azure")

        # Score label
        self.__score_label = tk.Label(self.__game_frame, text=f"Score: "
                                                              f"{self.__score}",
                                      font=(LETTER_TYPE, 16),
                                      fg=LETTERS_COLOR, bg="azure")
        self.__score_label.grid(row=1, column=0, padx=10, pady=10,
                                sticky="nsew")

        # Word label
        self.__word_label = tk.Label(self.__game_frame, text="Word: ",
                                     font=(LETTER_TYPE, 16), fg=LETTERS_COLOR,
                                     bg="azure")
        self.__word_label.grid(row=1, column=2, columnspan=2, padx=10,
                               pady=10, sticky="nsew")

        # Listbox
        self.__listbox = tk.Listbox(self.__game_frame, width=30, height=10)

        self.__listbox.grid(row=2, column=0, columnspan=2, padx=10,
                            pady=10,
                            rowspan=4, sticky="nsew")

        # Timer label
        self.timer_label = tk.Label(self.__game_frame, text="03:00",
                                    font=(LETTER_TYPE, 16), bg="azure")
        self.timer_label.grid(row=6, column=0, columnspan=2, padx=10,
                              pady=10, sticky="nsew")

        # Check button
        self.__check_button = tk.Button(self.__game_frame, text="Check",
                                        font=(LETTER_TYPE, 16),
                                        fg=LETTERS_COLOR,
                                        bg=BUTTON_REG_COLOR)
        self.__check_button.grid(row=6, column=2, padx=10, pady=6,
                                 sticky="nsew")

        # Boggle board frame
        self.__board_frame = tk.Frame(self.__game_frame, bg="lightblue")
        self.__board_frame.grid(row=2, column=2, columnspan=2, rowspan=4,
                                padx=10,
                                pady=10, sticky="nsew")

        # Generate buttons for the board
        self.__generate_boggle_board(self.__board_frame, randomize_board())

        # Exit Button for the frame
        self.__exit_button_f2 = tk.Button(self.__game_frame, text="Exit",
                                          font=(LETTER_TYPE, 10),
                                          fg=LETTERS_COLOR,
                                          bg=BUTTON_REG_COLOR)
        self.__exit_button_f2.grid(row=6, column=3, pady=10, padx=20,
                                   sticky="nsew")
        ###################################################################
        # Third Layer: Final Frame
        ###################################################################

        self.__final_frame = tk.Frame(self.__root)
        self.__second_canvas = tk.Canvas(self.__final_frame)

        self.__second_canvas.create_image(0, 0, image=self.__background_image,
                                          anchor="nw")
        self.__second_canvas.create_text(200, 100, text="Time is up!",
                                         fill=LETTERS_COLOR,
                                         font=(LETTER_TYPE, 32))

        self.__exit_button_f3 = tk.Button(self.__second_canvas, text="Exit",
                                          font=(LETTER_TYPE, 20),
                                          fg=LETTERS_COLOR,
                                          bg=BUTTON_REG_COLOR)
        self.__exit_button_f3.pack(side="bottom", pady=10, padx=20)

        self._play_again_button = tk.Button(self.__second_canvas,
                                            text="Play again",
                                            font=(LETTER_TYPE, 20),
                                            fg=LETTERS_COLOR,
                                            bg=BUTTON_REG_COLOR)
        self._play_again_button.pack(side="bottom", pady=40)
        self.__second_canvas.pack(expand=True, fill=tk.BOTH)

    ###########################################################################
    #                              GETTERS                                    #
    ###########################################################################

    def get_window(self):
        return self.__root

    def get_initial_frame(self):
        return self.__initial_frame

    def get_game_frame(self):
        return self.__game_frame

    def get_board_frame(self):
        return self.__board_frame

    def get_final_frame(self):
        return self.__final_frame

    def get_exit_f1(self):
        return self.__exit_button_f1

    def get_start_button(self):
        return self.__start_button

    def get_exit_button_f3(self):
        return self.__exit_button_f3

    def get_play_again_button(self):
        return self._play_again_button

    def get_exit_button_f2(self):
        return self.__exit_button_f2

    def get_countdown(self, minutes: int, seconds: int, frame):
        """Gets the private method countdown"""
        return self.__countdown(minutes, seconds, frame)

    def get_word_label(self):
        return self.__word_label

    def get_collected_path(self):
        return self.__collected_path

    def get__update_word_label_init(self):
        return self.__update_word_label()

    def get_update_timer_label(self, total_seconds):
        return self.__update_timer_label(total_seconds)

    def get_board_game(self):
        return self.__board_game

    def get_generate_boggle_board(self, board_frame, board):
        return self.__generate_boggle_board(board_frame, board)

    def get_listbox(self):
        return self.__listbox

    def get_found_word(self):
        return self.__found_words

    def get_check_button(self):
        return self.__check_button

    ###########################################################################
    #                               SETTERS                                   #
    ###########################################################################
    def set_found_word_empty(self):
        self.__found_words = []
        return self.__found_words

    def set_found_path_words_empty(self):
        self.__collected_path = []
        return self.__collected_path

    def set_updated_score(self):
        return self.__update_score_label(self.__collected_path)

    def set_score_label_zero(self):
        """Updates the score label to be 0 at the beginning of each game"""
        self.__score = 0
        self.__score_label.configure(text=f"Score: {self.__score}")

    def set_word_label_empty(self):
        self.__update_word_label()

    def set_collected_path_empty(self):
        self.__collected_path = []
        return self.__collected_path

    def set_score_to_zero(self):
        self.__score = 0

    def set_button_color_default(self):
        for button in self.lst_buttons:
            button.config(bg=BUTTON_REG_COLOR)

    ###########################################################################
    def run(self) -> None:
        """Starts the game loop"""
        self.__root.mainloop()

    def play_music(self):
        """Plays the music for the game"""
        mixer.init()
        mixer.music.load(self.music)
        mixer.music.play(loops=-1, start=0.3)

    def stop_music(self):
        """Stops the music"""
        mixer.music.stop()

    def __countdown(self, minutes: int, seconds: int,
                    frame_to_clear: tk.Frame) -> None:
        """Defines th countdown timer. When its up, the game frame closes
        and the final frame appears to the player.
        :param (int) minutes: how many minutes does the player has
        :param (int) seconds: how many seconds does the player has
        :param (tk.Frame) frame_to_clear: which frame will be deleted when
        the time is up"""
        total_seconds = minutes * 60 + seconds

        # Update the timer label initially
        self.__update_timer_label(total_seconds)

        if total_seconds >= 0:
            # Schedule the next update after 1 second
            self.__root.after(1000, self.__countdown, minutes, seconds
                              - 1,
                              frame_to_clear)
        else:
            # Game over, clear the game frame and show the final frame
            self.stop_music()
            self.__game_frame.pack_forget()
            self.__board_frame.pack_forget()
            # self.__stop_music(self)
            self.__final_frame.pack(side=tk.TOP, fill=tk.BOTH,
                                    expand=True)

        self.__root.update()

    def __update_timer_label(self, total_seconds: int) -> None:
        """Update the Timer Label with the current time
        :param (int) total_seconds: full time in seconds that the player has"""
        minutes = total_seconds // 60
        seconds = total_seconds % 60
        timer_text = f"Timer {minutes:02}:{seconds:02}"
        self.timer_label.configure(text=timer_text,
                                   font=(LETTER_TYPE, 15),
                                   fg=LETTERS_COLOR, padx=10)

    def __update_score_label(self, path) -> None:
        """Updates the Score Label of the player when finding a new valid word
        :param (list) path: A list to tuples."""
        self.__score += (len(path) ** 2)
        self.__score_label.configure(text=f"Score {self.__score}",
                                     font=(LETTER_TYPE, 15),
                                     fg=LETTERS_COLOR, padx=10)

    def __update_word_label(self):
        """Updates the word label to be empty at the beginning of each game"""
        self.__word_label.configure(text="Word: ")

    def button_click(self, row, column, button) -> None:
        """Commands what pressing each button does. Changes its color and
        adds the letter to the collected path of letters and to the display
        of the word created"""
        current_color = button['bg']
        if current_color == BUTTON_ACTIVE_COLOR:
            button.config(
                bg=BUTTON_REG_COLOR)  # Change back to the default color
            # Get the button's text
            text = button['text']
            new_word = rreplace(self.__word_label["text"], text, "", 1)
            self.__word_label.config(text=new_word)
            self.__collected_path.remove((row, column))

        else:
            button.config(bg=BUTTON_ACTIVE_COLOR)
            # Get the button's text
            text = button['text']

            # Update the selected letters
            self.__collected_path.append((row, column))

            # Update the word label with the pressed button's text
            current_word = self.__word_label['text']
            new_word = current_word + text
            self.__word_label.config(text=new_word)

    def __generate_boggle_board(self, parent_frame, board):
        """Create a board for the game. Converts the letter to bottoms."""
        self.__board_game = board
        for row in range(4):
            for col in range(4):
                letter = board[row][col]
                button = tk.Button(parent_frame, text=letter, width=5,
                                   height=2, font=(LETTER_TYPE, 10),
                                   bg=BUTTON_REG_COLOR)
                button.grid(row=row, column=col, padx=5, pady=5)
                button.config(
                    command=lambda i=row, j=col, btn=button:
                    self.button_click(i, j, btn))

                self.lst_buttons.append(button)


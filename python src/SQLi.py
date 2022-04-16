import joblib
import pickle

# Load the model from the file
randomForest_from_joblib = joblib.load('RandomForest.pkl')
myvectorizer = pickle.load(open("myVectorizer", 'rb'))


def clean_data(input_val):
    input_val = input_val.replace('\n', '')
    input_val = input_val.replace('%20', ' ')
    input_val = input_val.replace('=', ' = ')
    input_val = input_val.replace('((', ' (( ')
    input_val = input_val.replace('))', ' )) ')
    input_val = input_val.replace('(', ' ( ')
    input_val = input_val.replace(')', ' ) ')

    return input_val


def predict_sqli_attack():
    repeat = True

    space = ''
    for i in range(20):
        space += "-"

    print(space)
    input_val = input("Please enter some data: ")
    print(space)

    if input_val == '0':
        repeat = False

    input_val = clean_data(input_val)
    input_val = [input_val]

    input_val = myvectorizer.transform(input_val).toarray()
    result = randomForest_from_joblib.predict(input_val)

    print(space)

    if repeat == True:

        if result == 1:
            print("ALERT! This could be SQL injection attack!")


        elif result == 0:
            print("It seems to be a benign")

        print(space)

        predict_sqli_attack()

    elif repeat == False:
        print("closing ")



predict_sqli_attack()
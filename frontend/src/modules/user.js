import { setApiHeaders, setFileApiHeaders } from "api/api";
import { checkAttend } from "api/attendance";
import { loginUser, getDept } from "api/user";
import { readTimeTable } from "./timetable";

const LOGIN_SUCCESS = "USER/LOGIN_SUCCESS";
const LOGIN_FAIL = "USER/LOGIN_FAIL";
const LOGOUT_SUCCESS = "USER/LOGOUT_SUCCESS";
const ATTEND_SUCCESS = "USER/ATTEND_SUCCESS";
const FINISH_SUCCESS = "USER/FINISH_SUCCESS";
const UPDATE_SUCCESS = "USER/UPDATE_SUCCESS";
const UPDATE_FAIL = "USER/UPDATE_FAIL";

export const login = (user, isChecked) => async (dispatch) => {
  loginUser(user)
    .then((response) => {
      if (response.data) {
        if (isChecked) {
          localStorage.setItem("access-token", response.data.accessToken);
        } else {
          sessionStorage.setItem("access-token", response.data.accessToken);
        }

        const userId = response.data.userId;
        let isAttend = false;
        setApiHeaders();
        setFileApiHeaders();

        getDept(userId).then((response) => {
          const data = response.data;
          checkAttend(userId).then((response) => {
            if (response.data.length > 0) {
              isAttend = true;
            }
            dispatch({
              type: LOGIN_SUCCESS,
              userId: userId,
              data: data,
              isAttend: isAttend,
            });
          });
        });
      }
      dispatch(readTimeTable());
    })
    .catch(() => {
      dispatch({
        type: LOGIN_FAIL,
        error: true,
      });
    });
};

export const updateUserInfo = (userId) => async (dispatch) => {
  getDept(userId)
    .then((res) => {
      dispatch({
        type: UPDATE_SUCCESS,
        userId: userId,
        data: res.data,
      });
    })
    .catch((e) => {
      dispatch({
        type: UPDATE_FAIL,
        error: true,
      });
    });
};

export const logout = () => {
  sessionStorage.removeItem("access-token");
  localStorage.removeItem("access-token");
  return {
    type: LOGOUT_SUCCESS,
  };
};

export const attendance = () => {
  return {
    type: ATTEND_SUCCESS,
  };
};

export const finish = () => {
  return {
    type: FINISH_SUCCESS,
  };
};

const initialState = {
  isLoggedIn: false,
  userId: "",
  userName: "",
  schoolName: "",
  gradeCode: "",
  classCode: "",
  studentNo: "",
  userCode: "",
  isAttend: false,
  error: false,
};

const user = (state = initialState, action) => {
  switch (action.type) {
    case LOGIN_SUCCESS:
      return {
        ...state,
        isLoggedIn: true,
        userId: action.userId,
        userName: action.data.userName,
        schoolName: action.data.schoolName,
        gradeCode: action.data.gradeCode,
        classCode: action.data.classCode,
        studentNo: action.data.studentNo,
        userCode: action.data.userCode,
        isAttend: action.data.isAttend,
      };
    case LOGIN_FAIL:
      return {
        ...state,
        isLoggedIn: false,
        error: true,
      };
    case LOGOUT_SUCCESS:
      return {
        ...state,
        isLoggedIn: false,
        userId: null,
        error: false,
      };

    case ATTEND_SUCCESS: {
      return {
        ...state,
        isAttend: true,
      };
    }

    case FINISH_SUCCESS: {
      return {
        ...state,
        isAttend: false,
      };
    }

    case UPDATE_SUCCESS: {
      return {
        ...state,
        schoolName: action.data.schoolName,
        gradeCode: action.data.gradeCode,
        classCode: action.data.classCode,
        studentNo: action.data.studentNo,
        userCode: action.data.userCode,
      };
    }
    default:
      return state;
  }
};

export default user;

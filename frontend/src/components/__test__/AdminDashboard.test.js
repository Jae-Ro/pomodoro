import React from 'react';
import ReactDOM from 'react-dom';
import AdminDashboard from '../AdminDashboard';
import renderer from 'react-test-renderer';
import {shallow} from 'enzyme';
import toJSON from 'enzyme-to-json';
import { Provider } from "react-redux";
import configureMockStore from "redux-mock-store";

const mockStore = configureMockStore();
const store = mockStore({});


test("AdminDashboard renders correctly",()=>{
    const wrapper = shallow(<Provider store={store}> <AdminDashboard /> </Provider>)

    expect(toJSON(wrapper)).toMatchSnapshot();
})

import {actionCreators} from '../../models/actions/userModalActions';
// import * as types from '../../constants/ActionTypes'
import { actionTypes } from '../../models/actions/userModalActions';


describe('AdminDashboard actions', () => {
  it('should create a user', () => {
    const data = { firstName: 'Sherlock', lastName: 'Holmes', email: 'sherlock@gmail.com' }

    const expectedAction = {
        userData: data,
        type: actionTypes.SUBMIT_USER_MODAL
    }
    expect(actionCreators.submitUserModal(data)).toEqual(expectedAction)
  })
  it('should edit a user', () => {
    const user = {id: "123456", firstName: 'Sherlock', lastName: 'Holmes', email: 'sherlock@gmail.com' }
    const expectedAction = {
        userId: user.id,
        userFn: user.firstName,
        userLn: user.lastName,
        userEmail: user.email,
        type: actionTypes.EDIT_USER_MODAL
    }
    expect(actionCreators.editUserModal(user)).toEqual(expectedAction)
  })
  it('should delete a user', () => {
    const id = '123456'
    const expectedAction = {
        userId: id,
        type: actionTypes.DELETE_USER_MODAL
    }
    expect(actionCreators.deleteUserModal(id)).toEqual(expectedAction)
  })

})

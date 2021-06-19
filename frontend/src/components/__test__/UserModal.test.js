import React from 'react';
import ReactDOM from 'react-dom';
import UserModal from '../UserModal';
import renderer from 'react-test-renderer';
import {shallow} from 'enzyme';
import toJSON from 'enzyme-to-json';
import { Provider } from "react-redux";
import configureMockStore from "redux-mock-store";

const mockStore = configureMockStore();
const store = mockStore({});


test("UserModal matches its snapshot",()=>{
    const wrapper = shallow(<Provider store={store}> <UserModal /> </Provider>)

    expect(toJSON(wrapper)).toMatchSnapshot();
})

import {actionCreators} from '../../models/actions/userModalActions';
// import * as types from '../../constants/ActionTypes'
import { actionTypes } from '../../models/actions/userModalActions';


describe('UserModal actions', () => {
  it('should open a user modal', () => {
    const expectedAction = {
        type: actionTypes.OPEN_USER_MODAL
    }
    expect(actionCreators.openUserModal()).toEqual(expectedAction)
  })
  it('should close a user modal', () => {
    const expectedAction = {
        type: actionTypes.CLOSE_USER_MODAL
    }
    expect(actionCreators.closeUserModal()).toEqual(expectedAction)
  })

})